package com.zlrx.thesis.subjectservice.service

import com.zlrx.thesis.subjectservice.api.enrollment.EnrollmentIntegration
import com.zlrx.thesis.subjectservice.api.grade.GradeIntegration
import com.zlrx.thesis.subjectservice.config.ApiException
import com.zlrx.thesis.subjectservice.config.Messages.SUBJECT_NOT_FOUND
import com.zlrx.thesis.subjectservice.config.Messages.SUBJECT_NO_CLASS
import com.zlrx.thesis.subjectservice.controller.model.StudentProgress
import com.zlrx.thesis.subjectservice.controller.model.SubjectSaveRequest
import com.zlrx.thesis.subjectservice.controller.model.SubjectWithGrades
import com.zlrx.thesis.subjectservice.domain.Subject
import com.zlrx.thesis.subjectservice.domain.newId
import com.zlrx.thesis.subjectservice.repository.SubjectRepository
import com.zlrx.thesis.subjectservice.repository.findByIdOrThrow
import org.springframework.core.io.Resource
import org.springframework.core.io.ResourceLoader
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import java.nio.file.Files
import java.nio.file.Path

private const val DIR_PATH = "/data/img/"

@Service
@Transactional
class TeacherSubjectService(
    private val repository: SubjectRepository,
    private val resourceLoader: ResourceLoader,
    private val enrollmentIntegration: EnrollmentIntegration,
    private val gradeIntegration: GradeIntegration
) : BaseService() {

    fun findAll(includeArchive: Boolean, page: PageRequest): Page<Subject> {
        return if (includeArchive) {
            val subjectsPage = withUserFilter { repository.findAllBy(page) }
            enhanceWithOccupiedSeats(subjectsPage)
        } else {
            val subjectsPage = withUserFilter { repository.findAllByArchiveFalse(page) }
            enhanceWithOccupiedSeats(subjectsPage)
        }
    }

    fun findAllMySubject(): List<Subject> = withUserFilter {
        repository.findAll(
            Sort.by(Sort.Direction.ASC, "name")
        )
    }

    fun subjectWithGrades(id: String): SubjectWithGrades {
        val subject = withUserFilter { repository.findByIdOrThrow(id) }
        val grades = gradeIntegration.getSubjectGrades(subject.id)
        return SubjectWithGrades(
            name = subject.name!!,
            grades = grades
        )
    }

    fun getStudentsForSubject(subjectId: String): List<StudentProgress> {
        val subject = withUserFilter { repository.findByIdOrThrow(subjectId) }
        val users = enrollmentIntegration.enrolledBy(subject.id)
        val progress = gradeIntegration.getStudentsProgressForSubject(subjectId)

        return users.map {
            StudentProgress(it.id, it.name, progress[it.id] ?: 0)
        }
    }

    private fun enhanceWithOccupiedSeats(page: Page<Subject>): Page<Subject> {
        val subjectIds = page.content.map { it.id }
        val occupiedSeats = getOccupiedSeats(subjectIds)
        page.content.forEach {
            it.occupiedSeats = occupiedSeats[it.id]
        }
        return page
    }

    private fun getOccupiedSeats(subjectIds: List<String>): Map<String, Int?> {
        return subjectIds.associateWith { enrollmentIntegration.enrollCount(it) }
    }

    fun findById(id: String): Subject {
        return withUserFilter {
            repository.findOneById(id)
        } ?: throw ApiException(SUBJECT_NOT_FOUND, HttpStatus.NOT_FOUND, id)
    }

    fun publish(id: String) {
        val subject = withUserFilter { repository.findOneById(id) }
        if (subject != null && subject.lessons.isNotEmpty()) {
            subject.published = true
            repository.save(subject)
        } else {
            throw ApiException(SUBJECT_NO_CLASS)
        }
    }

    fun saveFile(multipartFile: MultipartFile?): String? =
        multipartFile?.let { file ->
            val fileName = file.originalFilename
            val newFilename = "${newId()}-$fileName"
            checkAndCreateDir()
            file.transferTo(
                Path.of("$DIR_PATH$newFilename")
            )
            newFilename
        }

    private fun checkAndCreateDir() {
        val path = Path.of(DIR_PATH)
        Files.createDirectories(path)
    }

    fun loadFile(fileName: String): Resource {
        return resourceLoader.getResource("file:$DIR_PATH$fileName")
    }

    fun saveSubject(request: SubjectSaveRequest): Subject {
        val subject = mapToSubject(Subject(), request)
        val saved = repository.save(subject)
        return repository.findOneById(saved.id)!!
    }

    fun updateSubject(id: String, subjectRequest: SubjectSaveRequest): Subject {
        val subject = withUserFilter { repository.findByIdOrThrow(id) }
        val updated = mapToSubject(subject, subjectRequest)
        val saved = repository.save(updated)
        return repository.findOneById(saved.id)!!
    }

    fun archive(id: String, archive: Boolean): Subject {
        val subject = withUserFilter { repository.findByIdOrThrow(id) }
        subject.archive = archive
        repository.save(subject)
        return withUserFilter { repository.findOneById(id)!! }
    }

    private fun mapToSubject(subject: Subject, request: SubjectSaveRequest): Subject {
        subject.also {
            it.name = request.name
            it.description = request.description
            it.credit = request.credit
            it.seats = request.seats
            it.startDt = request.startDt
            it.endDt = request.endDt
            it.coverImagePath = request.coverImagePath ?: it.coverImagePath
        }
        return subject
    }
}
