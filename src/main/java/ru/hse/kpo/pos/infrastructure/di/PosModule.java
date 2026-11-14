package ru.hse.kpo.pos.infrastructure.di;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import ru.hse.kpo.pos.application.PosFacade;
import ru.hse.kpo.pos.application.port.out.*;
import ru.hse.kpo.pos.application.usecase.*;
import ru.hse.kpo.pos.domain.model.Rubricator;
import ru.hse.kpo.pos.domain.model.EvaluationFactory;
import ru.hse.kpo.pos.domain.service.AllCriteriaWeightedStrategy;
import ru.hse.kpo.pos.domain.service.FinalScoreService;
import ru.hse.kpo.pos.infrastructure.console.ConsoleApp;
import ru.hse.kpo.pos.infrastructure.export.CsvResultExporter;
import ru.hse.kpo.pos.infrastructure.persistence.*;
import java.nio.file.Path;

public final class PosModule extends AbstractModule {
    @Provides
    @Singleton
    StudentRepository provideStudentRepository() {
        return new InMemoryStudentRepository();
    }

    @Provides
    @Singleton
    EvaluationRepository provideEvaluationRepository() {
        return new InMemoryEvaluationRepository();
    }

    @Provides
    @Singleton
    Rubricator provideRubricator() {
        return new Rubricator();
    }

    @Provides
    FinalScoreService provideFinalScoreService() {
        return new FinalScoreService(new AllCriteriaWeightedStrategy());
    }

    @Provides
    ResultExporter provideResultExporter() {
        return new CsvResultExporter(Path.of("results.csv"));
    }

    @Provides
    StudentService provideStudentService(StudentRepository repository) {
        return new StudentService(repository);
    }

    @Provides
    RubricatorService provideRubricatorService(Rubricator rubricator) {
        return new RubricatorService(rubricator);
    }

    @Provides
    EvaluationFactory provideEvaluationFactory(Rubricator rubricator) {
        return new EvaluationFactory(rubricator);
    }

    @Provides
    EvaluationService provideEvaluationService(EvaluationRepository evaluations, StudentRepository students, EvaluationFactory factory) {
        return new EvaluationService(evaluations, students, factory);
    }

    @Provides
    CalculateFinalScoreUseCase provideFinalScoreUseCase(StudentRepository students, EvaluationRepository evaluations, Rubricator rubricator, FinalScoreService calculator) {
        return new CalculateFinalScoreUseCase(students, evaluations, rubricator, calculator);
    }

    @Provides
    ExportResultsUseCase provideExportResultsUseCase(StudentRepository students, CalculateFinalScoreUseCase calculator, ResultExporter exporter) {
        return new ExportResultsUseCase(students, calculator, exporter);
    }

    @Provides
    CourseAnalyticsUseCase provideCourseAnalyticsUseCase(StudentRepository students, CalculateFinalScoreUseCase calculator) {
        return new CourseAnalyticsUseCase(students, calculator);
    }

    @Provides
    PosFacade providePosFacade(StudentService students, RubricatorService rubricator, EvaluationService evaluations, CalculateFinalScoreUseCase calculator, ExportResultsUseCase exporter, CourseAnalyticsUseCase analytics) {
        return new PosFacade(students, rubricator, evaluations, calculator, exporter, analytics);
    }

    @Provides
    ConsoleApp provideConsoleApp(PosFacade facade) {
        return new ConsoleApp(facade);
    }
}
