package ru.hse.kpo.pos.infrastructure.console;

import ru.hse.kpo.pos.application.PosFacade;
import ru.hse.kpo.pos.application.CriterionInput;
import ru.hse.kpo.pos.domain.model.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Scanner;

public final class ConsoleApp {
    private final PosFacade facade;
    private final Scanner scanner = new Scanner(System.in);

    public ConsoleApp(PosFacade facade) {
        this.facade = Objects.requireNonNull(facade);
    }

    public void run() {
        while (true) {
            printMenu();

            try {
                String command = input("Выберите действие: ");

                try {
                    switch (command) {
                        case "1" -> addStudent();
                        case "2" -> showStudents();
                        case "3" -> addCriterion();
                        case "4" -> showCriteria();
                        case "5" -> setScore();
                        case "6" -> showStudentResult();
                        case "7" -> exportResults();
                        case "8" -> cancelScore();
                        case "9" -> showCourseStatistics();
                        case "10" -> renameStudent();
                        case "11" -> addCriteriaBatch();
                        case "0" -> {
                            System.out.println("Завершение работы.");
                            return;
                        }
                        default -> System.out.println("Неизвестная команда.");
                    }
                } catch (IllegalArgumentException | IllegalStateException e) {
                    System.out.println("Ошибка: " + e.getMessage());
                }
            } catch (NoSuchElementException e) {
                return;
            }
        }
    }

    private void printMenu() {
        System.out.println("\n=== ПОС: Преподавательская оценка студентов ===");
        System.out.println("1. Добавить студента");
        System.out.println("2. Список студентов");
        System.out.println("3. Добавить критерий");
        System.out.println("4. Список критериев");
        System.out.println("5. Выставить или исправить оценку");
        System.out.println("6. Оценки и итог студента");
        System.out.println("7. Экспорт итогов в CSV");
        System.out.println("8. Отменить оценку");
        System.out.println("9. Статистика по курсу");
        System.out.println("10. Изменить имя студента");
        System.out.println("11. Добавить набор критериев");
        System.out.println("0. Выход");
    }

    private String input(String message) {
        System.out.print(message);
        return scanner.nextLine().trim();
    }

    private int inputInt(String message) {
        return Integer.parseInt(input(message));
    }

    private void addStudent() {
        Student student = facade.addStudent(input("Имя студента: "));
        System.out.println("Студент добавлен: " + student.getName());
    }

    private void renameStudent() {
        Student student = selectStudent();
        String newName = input("Новое имя студента: ");
        Student updated = facade.renameStudent(student.getId(), newName);
        System.out.println("Имя изменено: " + updated.getName());
    }

    private void showStudents() {
        List<Student> students = facade.getStudents();

        if (students.isEmpty()) {
            System.out.println("Студентов пока нет.");
            return;
        }

        for (int i = 0; i < students.size(); i++) {
            System.out.println((i + 1) + ". " + students.get(i).getName());
        }
    }

    private void addCriteriaBatch() {
        int count = inputInt("Количество критериев: ");
        if (count <= 0) throw new IllegalArgumentException("Количество должно быть положительным");

        List<CriterionInput> inputs = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            System.out.println("Критерий " + (i + 1) + ":");
            String name = input("Название: ");
            int maxScore = inputInt("Максимальный балл: ");
            BigDecimal weight = new BigDecimal(input("Вес: ").replace(',', '.'));
            inputs.add(new CriterionInput(name, maxScore, weight));
        }

        int added = facade.addCriteria(inputs).size();
        System.out.println("Добавлено критериев: " + added);
    }

    private void addCriterion() {
        String name = input("Название критерия: ");
        int maxScore = inputInt("Максимальный балл: ");
        BigDecimal weight = new BigDecimal(input("Вес критерия: ").replace(',', '.'));

        Criterion criterion = facade.addCriterion(name, maxScore, weight);
        System.out.println("Критерий добавлен: " + criterion.getName());
    }

    private void showCriteria() {
        List<Criterion> criteria = facade.getCriteria();

        if (criteria.isEmpty()) {
            System.out.println("Критериев пока нет.");
            return;
        }

        for (int i = 0; i < criteria.size(); i++) {
            Criterion criterion = criteria.get(i);
            System.out.printf("%d. %s | Максимум: %d | Вес: %s%n", i + 1, criterion.getName(), criterion.getMaxScore(), criterion.getWeight());
        }
    }

    private Student selectStudent() {
        List<Student> students = facade.getStudents();
        if (students.isEmpty()) throw new IllegalStateException("Сначала добавьте студента");

        showStudents();
        int number = inputInt("Номер студента: ");
        if (number < 1 || number > students.size()) throw new IllegalArgumentException("Неверный номер студента");
        return students.get(number - 1);
    }

    private Criterion selectCriterion() {
        List<Criterion> criteria = facade.getCriteria();
        if (criteria.isEmpty()) throw new IllegalStateException("Сначала добавьте критерий");

        showCriteria();
        int number = inputInt("Номер критерия: ");
        if (number < 1 || number > criteria.size()) throw new IllegalArgumentException("Неверный номер критерия");
        return criteria.get(number - 1);
    }

    private void setScore() {
        Student student = selectStudent();
        Criterion criterion = selectCriterion();
        int score = inputInt("Балл: ");

        facade.setScore(student.getId(), criterion.getId(), score);
        System.out.println("Оценка успешно сохранена.");
    }

    private void exportResults() {
        System.out.println("Результаты экспортированы: " + facade.exportResults());
    }

    private void showCourseStatistics() {
        var statistics = facade.getCourseStatistics();
        System.out.println("Количество студентов: " + statistics.studentCount());
        System.out.println("Средний балл: " + statistics.average() + "%");
        System.out.println("Минимальный балл: " + statistics.minimum() + "%");
        System.out.println("Максимальный балл: " + statistics.maximum() + "%");
    }

    private void cancelScore() {
        Student student = selectStudent();
        Criterion criterion = selectCriterion();
        boolean removed = facade.cancelScore(student.getId(), criterion.getId());
        System.out.println(removed ? "Оценка отменена." : "Оценка по этому критерию не найдена.");
    }

    private void showStudentResult() {
        Student student = selectStudent();
        PosFacade.StudentResult result = facade.getStudentResult(student.getId());

        System.out.println("\nРезультаты студента: " + student.getName());

        for (Criterion criterion : facade.getCriteria()) {
            String score = result.evaluations().stream()
                .filter(e -> e.getCriterionId().equals(criterion.getId()))
                .map(e -> String.valueOf(e.getScore().value()))
                .findFirst().orElse("не выставлен");

            System.out.printf("%s: %s / %d%n", criterion.getName(), score, criterion.getMaxScore());
        }

        System.out.println("Итоговый балл: " + result.finalScore() + "%");
    }
}
