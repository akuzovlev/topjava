package ru.javawebinar.topjava.repository.jpa;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.repository.MealRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.time.LocalDateTime;
import java.util.List;

@Repository
@Transactional(readOnly = true)
public class JpaMealRepositoryImpl implements MealRepository {

    @PersistenceContext
    private EntityManager em;

    @Override
    @Transactional
    public Meal save(Meal meal, int userId) {

        if (meal.isNew()) {
            meal.setUser(em.find(User.class,userId));
            em.persist(meal);
            return meal;
        } else {

            Meal m = em.find(Meal.class, meal.getId());
            meal.setUser(m.getUser());
            if (meal.getUser().getId() != userId) {
                return null;
            }
            return em.merge(meal);
        }
    }

    @Override
    @Transactional
    public boolean delete(int id, int userId) {

        return em.createNamedQuery(Meal.DELETE)
                .setParameter("id", id).setParameter("uid",userId)
                .executeUpdate() != 0;
    }

    @Override
    public Meal get(int id, int userId) {
        Meal m = em.find(Meal.class, id);
        if (m.getUser().getId() != userId) {
            return null;
        }
        return m;
    }

    @Override
    public List<Meal> getAll(int userId) {
        return em.createNamedQuery(Meal.GET_ALL).setParameter("uid",userId).getResultList();
    }

    @Override
    public List<Meal> getBetween(LocalDateTime startDate, LocalDateTime endDate, int userId) {
        return em.createNamedQuery(Meal.GET_BETWEEN).setParameter("startDate",startDate).setParameter("endDate",endDate).getResultList();
    }
}