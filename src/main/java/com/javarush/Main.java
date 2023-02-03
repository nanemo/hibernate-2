package com.javarush;

import com.javarush.domain.*;
import com.javarush.properties.Property;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Main {
    private final SessionFactory sessionFactory;
    private final Property getDao;

    public Main() {
        sessionFactory = new Property().getSessionFactory();
        getDao = new Property();
    }

    public static void main(String[] args) {
        Main main = new Main();
        Customer customer = main.createCustomer();
        main.customerGetBackRentedFilm();
        main.customerRentInventory(customer);

        main.newFilmWasMade();
    }

    private void newFilmWasMade() {
        try (Session session = sessionFactory.getCurrentSession()) {
            session.beginTransaction();

            Language language = getDao.getLanguageDao().getItems(0, 20).stream().unordered().findAny().get();
            List<Category> categories = getDao.getCategoryDao().getItems(0, 5);
            List<Actor> actors = getDao.getActorDao().getItems(0, 20);

            Film film = new Film();
            film.setActors(new HashSet<>(actors));
            film.setRating(Rating.NC17);
            film.setSpecialFeatures(Set.of(Feature.TRAILERS, Feature.COMMENTARIES));
            film.setLength((short) 123);
            film.setReplacementCost(BigDecimal.TEN);
            film.setRentalRate(BigDecimal.ZERO);
            film.setLanguage(language);
            film.setDescription("new scary film");
            film.setTitle("scary my-movie");
            film.setRentalDuration((byte) 40);
            film.setOriginalLanguage(language);
            film.setCategories(new HashSet<>(categories));
            film.setReleaseYear(Year.now());
            getDao.getFilmDao().save(film);

            FilmText filmText = new FilmText();
            filmText.setId(film.getId());
            filmText.setFilm(film);
            filmText.setDescription("new scary film");
            filmText.setTitle("scary my-movie");
            getDao.getFilmTextDao().save(filmText);

            session.getTransaction().commit();
        }
    }

    public Customer createCustomer() {
        try (Session session = sessionFactory.getCurrentSession()) {
            session.beginTransaction();
            Store store = getDao.getStoreDao().getItems(0, 1).get(0);

            City city = getDao.getCityDao().getByName("Baku");

            Address address = new Address();
            address.setAddress("Ataturk pr. 4A");
            address.setPhone("+994553058922");
            address.setCity(city);
            address.setDistrict("dsdsd");
            getDao.getAddressDao().save(address);

            Customer customer = new Customer();
            customer.setStore(store);
            customer.setFirstName("Nadir");
            customer.setLastName("Nadirli");
            customer.setAddress(address);
            customer.setIsActive(true);
            customer.setEmail("nanemodeveloper@gmail.com");
            getDao.getCustomerDao().save(customer);

            session.getTransaction().commit();
            return customer;
        }
    }

    public Rental customerGetBackRentedFilm() {
        try (Session session = sessionFactory.getCurrentSession()) {
            session.beginTransaction();

            Rental rental = getDao.getRentalDao().getRentalByCustomerId();

            Payment payment = getDao.getPaymentDao().getPaymentByRentalId(rental.getId());

            if (payment.getAmount().compareTo(BigDecimal.ZERO) == 0) {
                payment.setAmount(rental.getInventory().getFilm().getRentalRate());
            }
            rental.setReturnDate(LocalDateTime.now());
            getDao.getRentalDao().save(rental);

            session.getTransaction().commit();
            return rental;
        }
    }

    public void customerRentInventory(Customer customer) {
        try (Session session = sessionFactory.getCurrentSession()) {
            session.beginTransaction();

            Film film = getDao.getFilmDao().getFirstAvailableFilmForRent();
            Store store = getDao.getStoreDao().getItems(0, 1).get(0);

            Inventory inventory = new Inventory();
            inventory.setFilm(film);
            inventory.setStore(store);
            getDao.getInventoryDao().save(inventory);

            Staff staff = store.getStaff();
            Rental rental = new Rental();
            rental.setRentalDate(LocalDateTime.now());
            rental.setCustomer(customer);
            rental.setInventory(inventory);
            rental.setStaff(staff);
            getDao.getRentalDao().save(rental);

            Payment payment = new Payment();
            payment.setCustomer(customer);
            payment.setPaymentDate(LocalDateTime.now());
            payment.setRental(rental);
            payment.setStaff(staff);
            payment.setAmount(BigDecimal.valueOf(55.77));
            getDao.getPaymentDao().save(payment);

            session.getTransaction().commit();

        }
    }
}
