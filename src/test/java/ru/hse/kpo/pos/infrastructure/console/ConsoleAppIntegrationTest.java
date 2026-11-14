package ru.hse.kpo.pos.infrastructure.console;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import ru.hse.kpo.pos.application.PosFacade;
import ru.hse.kpo.pos.application.port.out.*;
import ru.hse.kpo.pos.application.usecase.*;
import ru.hse.kpo.pos.domain.model.*;
import ru.hse.kpo.pos.domain.service.*;
import ru.hse.kpo.pos.infrastructure.export.CsvResultExporter;
import ru.hse.kpo.pos.infrastructure.persistence.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import static org.junit.jupiter.api.Assertions.*;

class ConsoleAppIntegrationTest {
    @TempDir
    Path tempDir;

    private String runConsole(String input, Path csvPath) throws Exception {
        StudentRepository students = new InMemoryStudentRepository();
        EvaluationRepository evaluations = new InMemoryEvaluationRepository();
        Rubricator rubricator = new Rubricator();

        CalculateFinalScoreUseCase calculator = new CalculateFinalScoreUseCase(
            students, evaluations, rubricator,
            new FinalScoreService(new AllCriteriaWeightedStrategy())
        );

        PosFacade facade = new PosFacade(
            new StudentService(students),
            new RubricatorService(rubricator),
            new EvaluationService(evaluations, students, new EvaluationFactory(rubricator)),
            calculator,
            new ExportResultsUseCase(students, calculator, new CsvResultExporter(csvPath)),
            new CourseAnalyticsUseCase(students, calculator)
        );

        InputStream originalInput = System.in;
        PrintStream originalOutput = System.out;
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        try (PrintStream printStream = new PrintStream(output, true, StandardCharsets.UTF_8)) {
            System.setIn(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
            System.setOut(printStream);
            new ConsoleApp(facade).run();
        } finally {
            System.setIn(originalInput);
            System.setOut(originalOutput);
        }

        return output.toString(StandardCharsets.UTF_8);
    }

    @Test
    void completeScenarioCreatesStudentGradesAndExportsResults() throws Exception {
        String input = String.join("\n",
            "1", "Иван Иванов",
            "3", "Домашнее задание", "10", "2",
            "3", "Экзамен", "20", "1",
            "5", "1", "1", "8",
            "5", "1", "2", "10",
            "6", "1",
            "7",
            "0", ""
        );

        Path csvPath = tempDir.resolve("results.csv");
        String output = runConsole(input, csvPath);

        assertTrue(output.contains("Студент добавлен: Иван Иванов"));
        assertTrue(output.contains("Итоговый балл: 70.00%"));
        assertTrue(output.contains("Результаты экспортированы:"));
        assertTrue(Files.exists(csvPath));

        String csv = Files.readString(csvPath, StandardCharsets.UTF_8);
        assertTrue(csv.contains("\"Иван Иванов\";70.00"));
    }

    @Test
    void invalidScoreDoesNotBreakApplication() throws Exception {
        String input = String.join("\n",
            "1", "Иван Иванов",
            "3", "Экзамен", "10", "1",
            "5", "1", "1", "11",
            "6", "1",
            "0", ""
        );

        String output = runConsole(input, tempDir.resolve("results.csv"));

        assertTrue(output.contains("Ошибка: Балл превышает максимум критерия"));
        assertTrue(output.contains("Экзамен: не выставлен / 10"));
        assertTrue(output.contains("Итоговый балл: 0.00%"));
        assertTrue(output.contains("Завершение работы."));
    }
}
