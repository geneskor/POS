package ru.hse.kpo.pos.application;

import ru.hse.kpo.pos.application.usecase.*;
import ru.hse.kpo.pos.domain.model.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public final class PosFacade {
    private final StudentService studentService;
    private final RubricatorService rubricatorService;
    private final EvaluationService evaluationService;
    private final CalculateFinalScoreUseCase finalScoreUseCase;
    private final ExportResultsUseCase exportResultsUseCase;
    private final CourseAnalyticsUseCase courseAnalyticsUseCase;

    public PosFacade(StudentService studentService, RubricatorService rubricatorService, EvaluationService evaluationService, CalculateFinalScoreUseCase finalScoreUseCase, ExportResultsUseCase exportResultsUseCase, CourseAnalyticsUseCase courseAnalyticsUseCase) {
        this.studentService = Objects.requireNonNull(studentService);
        this.rubricatorService = Objects.requireNonNull(rubricatorService);
        this.evaluationService = Objects.requireNonNull(evaluationService);
        this.finalScoreUseCase = Objects.requireNonNull(finalScoreUseCase);
        this.exportResultsUseCase = Objects.requireNonNull(exportResultsUseCase);
        this.courseAnalyticsUseCase = Objects.requireNonNull(courseAnalyticsUseCase);
    }

    public Student addStudent(String name) {
        return studentService.addStudent(name);
    }

    public Student renameStudent(UUID studentId, String newName) {
        return studentService.renameStudent(studentId, newName);
    }

    public List<Student> getStudents() {
        return studentService.getStudents();
    }

    public Criterion addCriterion(String name, int maxScore, BigDecimal weight) {
        return rubricatorService.addCriterion(name, maxScore, weight);
    }

    public List<Criterion> addCriteria(List<CriterionInput> inputs) {
        return rubricatorService.addCriteria(inputs);
    }

    public List<Criterion> getCriteria() {
        return rubricatorService.getCriteria();
    }

    public void setScore(UUID studentId, UUID criterionId, int score) {
        evaluationService.setScore(studentId, criterionId, score);
    }

    public boolean cancelScore(UUID studentId, UUID criterionId) {
        return evaluationService.cancelScore(studentId, criterionId);
    }

    public StudentResult getStudentResult(UUID studentId) {
        List<Evaluation> evaluations = evaluationService.getStudentEvaluations(studentId);
        BigDecimal finalScore = finalScoreUseCase.execute(studentId);
        return new StudentResult(evaluations, finalScore);
    }

    public CourseAnalyticsUseCase.Statistics getCourseStatistics() {
        return courseAnalyticsUseCase.execute();
    }

    public String exportResults() {
        return exportResultsUseCase.execute();
    }

    public record StudentResult(List<Evaluation> evaluations, BigDecimal finalScore) {}
}
