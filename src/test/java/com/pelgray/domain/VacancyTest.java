package com.pelgray.domain;

import com.pelgray.domain.employer.Employer;
import com.pelgray.domain.requirements.KeySkill;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class VacancyTest {
    private Vacancy vacancy;

    @BeforeClass
    public void setUpVacancy() {
        vacancy = new Vacancy();
        vacancy.name = "TestName";
        vacancy.employer = new Employer("TestEmployer", "TestURL");
        vacancy.key_skills = Arrays.asList(new KeySkill("skill1"), new KeySkill("skill2"),
                new KeySkill("skill3"));
        vacancy.specialization = new ArrayList<>(0);
    }

    @Test(description = "Получение списка значений полей объекта Vacancy")
    public void testGetFieldsDataListWithFieldsParams() throws ReflectiveOperationException {
        List<String> orderedFields = Arrays.asList("", "name", "employer", "alternate_url");
        List<Object> expectedList = Arrays.asList("", vacancy.name, vacancy.employer.toString(), "-");
        Assert.assertEquals(vacancy.getFieldsDataList(orderedFields), expectedList, "Некорректный вывод значений полей");
    }

    @Test(description = "Получение списка значений полей-списков объекта Vacancy")
    public void testGetFieldsDataListWithListParams() throws ReflectiveOperationException {
        List<String> orderedFields = Arrays.asList("key_skills", "specialization");
        List<Object> expectedList = Arrays.asList("skill1, skill2, skill3", "-");
        Assert.assertEquals(vacancy.getFieldsDataList(orderedFields), expectedList, "Некорректный вывод значений списков");
    }

    @Test(description = "Выброс исключения при попытке обратиться к несуществующему полю класса Vacancy",
            expectedExceptions = ReflectiveOperationException.class)
    public void testExceptionGetFieldsDataList() throws ReflectiveOperationException {
        vacancy.getFieldsDataList(Collections.singletonList("nameTestABC"));
    }
}
