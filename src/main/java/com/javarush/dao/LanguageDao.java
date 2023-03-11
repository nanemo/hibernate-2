package com.javarush.dao;

import com.javarush.domain.Language;
import org.hibernate.SessionFactory;

public class LanguageDao extends GenericDao<Language> {
    public LanguageDao(SessionFactory sessionFactory) {
        super(Language.class, sessionFactory);
    }
}
