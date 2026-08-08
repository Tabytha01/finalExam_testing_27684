package com.auca.library;

import com.auca.library.dao.LocationDao;
import com.auca.library.dao.UserDao;
import com.auca.library.domain.Location;
import com.auca.library.domain.User;
import com.auca.library.domain.enums.Gender;
import com.auca.library.domain.enums.LocationType;
import com.auca.library.domain.enums.Role;
import com.auca.library.service.LocationService;
import com.auca.library.service.UserService;
import com.auca.library.util.HibernateUtil;
import com.auca.library.util.PasswordUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.*;

public class UserServiceTest {

    private UserService userService;
    private UserDao userDao;
    private LocationDao locationDao;
    private User testUser;
    private Location village;
    private Location province;

    @Before
    public void setUp() {
        userService = new UserService();
        userDao = new UserDao();
        locationDao = new LocationDao();
        LocationService locationService = new LocationService();

        province = new Location();
        province.setLocationCode("UPROV-" + UUID.randomUUID());
        province.setLocationName("User Test Province");
        province.setLocationType(LocationType.PROVINCE);
        locationService.createLocation(province, null);

        Location district = new Location();
        district.setLocationCode("UDIST-" + UUID.randomUUID());
        district.setLocationName("User Test District");
        district.setLocationType(LocationType.DISTRICT);
        locationService.createLocation(district, province.getLocationId());

        Location sector = new Location();
        sector.setLocationCode("USEC-" + UUID.randomUUID());
        sector.setLocationName("User Test Sector");
        sector.setLocationType(LocationType.SECTOR);
        locationService.createLocation(sector, district.getLocationId());

        Location cell = new Location();
        cell.setLocationCode("UCELL-" + UUID.randomUUID());
        cell.setLocationName("User Test Cell");
        cell.setLocationType(LocationType.CELL);
        locationService.createLocation(cell, sector.getLocationId());

        village = new Location();
        village.setLocationCode("UVIL-" + UUID.randomUUID());
        village.setLocationName("User Test Village");
        village.setLocationType(LocationType.VILLAGE);
        locationService.createLocation(village, cell.getLocationId());

        testUser = new User();
        testUser.setFirstName("Test");
        testUser.setLastName("User");
        testUser.setGender(Gender.MALE);
        testUser.setPhoneNumber("0780000099");
        testUser.setUserName("testuser-" + UUID.randomUUID());
        testUser.setPassword(PasswordUtil.hash("secret"));
        testUser.setRole(Role.STUDENT);
        testUser.setVillageId(village.getLocationId());
        userDao.save(testUser);
    }

    @After
    public void tearDown() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        session.remove(session.merge(testUser));
        tx.commit();
        session.close();

        for (Location loc : locationDao.findAll()) {
            if (loc.getLocationCode().startsWith("UPROV-") || loc.getLocationCode().startsWith("UDIST-")
                    || loc.getLocationCode().startsWith("USEC-") || loc.getLocationCode().startsWith("UCELL-")
                    || loc.getLocationCode().startsWith("UVIL-")) {
                Session s = HibernateUtil.getSessionFactory().openSession();
                Transaction t = s.beginTransaction();
                s.remove(s.merge(loc));
                t.commit();
                s.close();
            }
        }
    }

    @Test
    public void validPersonId_returnsCorrectProvinceName() {
        String name = userService.getProvinceNameByPersonId(testUser.getPersonId());
        assertEquals(province.getLocationName(), name);
    }

    @Test
    public void authenticate_correctCredentials_returnsTrue() {
        assertTrue(userService.authenticate(testUser.getUserName(), "secret"));
    }

    @Test
    public void authenticate_wrongPassword_returnsFalse() {
        assertFalse(userService.authenticate(testUser.getUserName(), "wrongpassword"));
    }

    @Test
    public void authenticate_unknownUsername_returnsFalse() {
        assertFalse(userService.authenticate("nobody", "secret"));
    }

    @Test
    public void authenticate_nullOrBlankInput_returnsFalse() {
        assertFalse(userService.authenticate(null, "secret"));
        assertFalse(userService.authenticate("", "secret"));
        assertFalse(userService.authenticate(testUser.getUserName(), null));
        assertFalse(userService.authenticate(testUser.getUserName(), ""));
    }
}
