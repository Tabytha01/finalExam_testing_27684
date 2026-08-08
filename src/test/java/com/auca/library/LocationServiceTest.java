package com.auca.library;

import com.auca.library.dao.LocationDao;
import com.auca.library.domain.Location;
import com.auca.library.domain.enums.LocationType;
import com.auca.library.exception.DuplicateLocationCodeException;
import com.auca.library.service.LocationService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.*;

public class LocationServiceTest {

    private LocationService locationService;
    private LocationDao locationDao;
    private Location province;

    @Before
    public void setUp() {
        locationService = new LocationService();
        locationDao = new LocationDao();

        province = new Location();
        province.setLocationCode("PROV-" + UUID.randomUUID());
        province.setLocationName("Test Province");
        province.setLocationType(LocationType.PROVINCE);
        locationService.createLocation(province, null);
    }

    @After
    public void tearDown() {
        for (Location loc : locationDao.findAll()) {
            if (loc.getLocationCode().startsWith("PROV-") || loc.getLocationCode().startsWith("DIST-")
                    || loc.getLocationCode().startsWith("DUP-") || loc.getLocationCode().startsWith("VIL-")
                    || loc.getLocationCode().startsWith("SEC-") || loc.getLocationCode().startsWith("CELL-")) {
                org.hibernate.Session session = com.auca.library.util.HibernateUtil.getSessionFactory().openSession();
                org.hibernate.Transaction tx = session.beginTransaction();
                session.remove(session.merge(loc));
                tx.commit();
                session.close();
            }
        }
    }

    @Test
    public void createProvince_withNoParent_succeeds() {
        Location p = new Location();
        p.setLocationCode("PROV-" + UUID.randomUUID());
        p.setLocationName("Another Province");
        p.setLocationType(LocationType.PROVINCE);
        Location saved = locationService.createLocation(p, null);
        assertNotNull(saved.getLocationId());
    }

    @Test
    public void createDistrict_withValidProvinceParent_succeeds() {
        Location district = new Location();
        district.setLocationCode("DIST-" + UUID.randomUUID());
        district.setLocationName("Test District");
        district.setLocationType(LocationType.DISTRICT);
        Location saved = locationService.createLocation(district, province.getLocationId());
        assertNotNull(saved.getLocationId());
        assertEquals(province.getLocationId(), saved.getParentId());
    }

    @Test(expected = IllegalArgumentException.class)
    public void createDistrict_withMissingParent_throwsException() {
        Location district = new Location();
        district.setLocationCode("DIST-" + UUID.randomUUID());
        district.setLocationName("Orphan District");
        district.setLocationType(LocationType.DISTRICT);
        locationService.createLocation(district, UUID.randomUUID());
    }

    @Test(expected = DuplicateLocationCodeException.class)
    public void createLocation_duplicateLocationCode_throwsException() {
        Location duplicate = new Location();
        duplicate.setLocationCode(province.getLocationCode());
        duplicate.setLocationName("Duplicate");
        duplicate.setLocationType(LocationType.PROVINCE);
        locationService.createLocation(duplicate, null);
    }

    @Test
    public void validVillageId_returnsCorrectProvinceName() {
        Location district = new Location();
        district.setLocationCode("DIST-" + UUID.randomUUID());
        district.setLocationName("Test District");
        district.setLocationType(LocationType.DISTRICT);
        locationService.createLocation(district, province.getLocationId());

        Location sector = new Location();
        sector.setLocationCode("SEC-" + UUID.randomUUID());
        sector.setLocationName("Test Sector");
        sector.setLocationType(LocationType.SECTOR);
        locationService.createLocation(sector, district.getLocationId());

        Location cell = new Location();
        cell.setLocationCode("CELL-" + UUID.randomUUID());
        cell.setLocationName("Test Cell");
        cell.setLocationType(LocationType.CELL);
        locationService.createLocation(cell, sector.getLocationId());

        Location village = new Location();
        village.setLocationCode("VIL-" + UUID.randomUUID());
        village.setLocationName("Test Village");
        village.setLocationType(LocationType.VILLAGE);
        locationService.createLocation(village, cell.getLocationId());

        String provinceName = locationService.getProvinceNameByVillageId(village.getLocationId());
        assertEquals(province.getLocationName(), provinceName);
    }
}
