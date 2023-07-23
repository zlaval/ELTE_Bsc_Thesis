package org.springframework.data.mongodb.core

import com.mongodb.client.model.CountOptions
import com.mongodb.client.result.DeleteResult
import com.mongodb.client.result.UpdateResult
import com.zlrx.thesis.quizservice.config.authInfo
import com.zlrx.thesis.quizservice.domain.TenantAwareEntity
import mu.KotlinLogging
import org.bson.Document
import org.springframework.data.mongodb.MongoDatabaseFactory
import org.springframework.data.mongodb.core.aggregation.Aggregation
import org.springframework.data.mongodb.core.aggregation.AggregationOperationContext
import org.springframework.data.mongodb.core.aggregation.AggregationResults
import org.springframework.data.mongodb.core.aggregation.MatchOperation
import org.springframework.data.mongodb.core.convert.MongoConverter
import org.springframework.data.mongodb.core.query.Collation
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.UpdateDefinition
import org.springframework.util.ReflectionUtils
import java.util.stream.Stream
import kotlin.reflect.full.isSubclassOf

open class TenantAwareMongoTemplate(
    mongoDbFactory: MongoDatabaseFactory,
    private val mongoConverter: MongoConverter
) : MongoTemplate(mongoDbFactory, mongoConverter) {

    private val log = KotlinLogging.logger {}

    override fun executeQuery(
        query: Query,
        collectionName: String,
        documentCallbackHandler: DocumentCallbackHandler,
        preparer: CursorPreparer?
    ) {
        super.executeQuery(query.withTenant(null, collectionName), collectionName, documentCallbackHandler, preparer)
    }

    override fun <T : Any?> findAll(entityClass: Class<T>, collectionName: String): List<T> {
        val orgId = authInfo()?.tenantId
        return if (orgId != null && isEntityTenantAware(entityClass, collectionName)) {
            logTrace()
            super.find(Query.query(Criteria.where("tenantId").`is`(orgId)), entityClass, collectionName)
        } else {
            super.findAll(entityClass, collectionName)
        }
    }

    override fun exists(query: Query, entityClass: Class<*>?, collectionName: String): Boolean {
        logTrace()
        return super.exists(query.withTenant(entityClass, collectionName), entityClass, collectionName)
    }

    override fun <T : Any?> findDistinct(
        query: Query,
        field: String,
        collectionName: String,
        entityClass: Class<*>,
        resultClass: Class<T>
    ): List<T> {
        logTrace()
        return super.findDistinct(
            query.withTenant(entityClass, collectionName),
            field,
            collectionName,
            entityClass,
            resultClass
        )
    }

    override fun <S : Any, T : Any> findAndReplace(
        query: Query,
        replacement: S,
        options: FindAndReplaceOptions,
        entityType: Class<S>,
        collectionName: String,
        resultType: Class<T>
    ): T {
        logTrace()
        return super.findAndReplace(
            query.withTenant(entityType, collectionName),
            replacement,
            options,
            entityType,
            collectionName,
            resultType
        )
    }

    override fun <T : Any?> doStream(
        query: Query,
        entityType: Class<*>,
        collectionName: String,
        returnType: Class<T>
    ): Stream<T> {
        logTrace()
        return super.doStream(query.withTenant(entityType, collectionName), entityType, collectionName, returnType)
    }

    override fun doCount(collectionName: String, filter: Document, options: CountOptions): Long {
        logTrace()
        return super.doCount(collectionName, filter.withTenant(null, collectionName), options)
    }

    override fun doExactCount(collectionName: String, filter: Document, options: CountOptions): Long {
        logTrace()
        return super.doExactCount(collectionName, filter.withTenant(null, collectionName), options)
    }

    override fun doUpdate(
        collectionName: String,
        query: Query,
        update: UpdateDefinition,
        entityClass: Class<*>?,
        upsert: Boolean,
        multi: Boolean
    ): UpdateResult {
        logTrace()
        return super.doUpdate(
            collectionName,
            query.withTenant(entityClass, collectionName),
            update,
            entityClass,
            upsert,
            multi
        )
    }

    override fun <T : Any?> doRemove(
        collectionName: String,
        query: Query,
        entityClass: Class<T>?,
        multi: Boolean
    ): DeleteResult {
        logTrace()
        return super.doRemove(collectionName, query.withTenant(entityClass, collectionName), entityClass, multi)
    }

    override fun <T : Any?> doFindAndDelete(collectionName: String, query: Query, entityClass: Class<T>): List<T> {
        logTrace()
        return super.doFindAndDelete(collectionName, query.withTenant(entityClass, collectionName), entityClass)
    }

    override fun <T : Any> doFindOne(
        collectionName: String,
        query: Document,
        fields: Document,
        preparer: CursorPreparer,
        entityClass: Class<T>
    ): T {
        logTrace()
        return super.doFindOne(
            collectionName,
            query.withTenant(entityClass, collectionName),
            fields,
            preparer,
            entityClass
        )
    }

    override fun <T : Any?> doFind(
        collectionName: String,
        query: Document,
        fields: Document,
        entityClass: Class<T>,
        preparer: CursorPreparer
    ): List<T> {
        logTrace()
        return super.doFind(
            collectionName,
            query.withTenant(entityClass, collectionName),
            fields,
            entityClass,
            preparer
        )
    }

    override fun <S : Any?, T : Any?> doFind(
        collectionName: String,
        query: Document,
        fields: Document,
        sourceClass: Class<S>,
        targetClass: Class<T>,
        preparer: CursorPreparer
    ): List<T> {
        logTrace()
        return super.doFind(
            collectionName,
            query.withTenant(sourceClass, collectionName),
            fields,
            sourceClass,
            targetClass,
            preparer
        )
    }

    override fun <T : Any> doFindAndRemove(
        collectionName: String,
        query: Document,
        fields: Document,
        sort: Document,
        collation: Collation?,
        entityClass: Class<T>
    ): T {
        logTrace()
        return super.doFindAndRemove(
            collectionName,
            query.withTenant(entityClass, collectionName),
            fields,
            sort,
            collation,
            entityClass
        )
    }

    override fun <T : Any> doFindAndModify(
        collectionName: String,
        query: Document,
        fields: Document,
        sort: Document,
        entityClass: Class<T>,
        update: UpdateDefinition,
        options: FindAndModifyOptions?
    ): T {
        logTrace()
        return super.doFindAndModify(
            collectionName,
            query.withTenant(entityClass, collectionName),
            fields,
            sort,
            entityClass,
            update,
            options
        )
    }

    override fun <T : Any?> doFindAndReplace(
        collectionName: String,
        mappedQuery: Document,
        mappedFields: Document,
        mappedSort: Document,
        collation: com.mongodb.client.model.Collation?,
        entityType: Class<*>,
        replacement: Document,
        options: FindAndReplaceOptions,
        resultType: Class<T>
    ): T? {
        logTrace()
        return super.doFindAndReplace(
            collectionName,
            mappedQuery.withTenant(entityType, collectionName),
            mappedFields,
            mappedSort,
            collation,
            entityType,
            replacement,
            options,
            resultType
        )
    }

    override fun <O : Any?> doAggregate(
        aggregation: Aggregation,
        collectionName: String,
        outputType: Class<O>,
        context: AggregationOperationContext
    ): AggregationResults<O> {
        logTrace()
        return super.doAggregate(aggregation.withTenant(collectionName), collectionName, outputType, context)
    }

    override fun <O : Any?> aggregateStream(
        aggregation: Aggregation,
        collectionName: String,
        outputType: Class<O>,
        context: AggregationOperationContext?
    ): Stream<O> {
        logTrace()
        return super.aggregateStream(aggregation.withTenant(collectionName), collectionName, outputType, context)
    }

    private fun logTrace() =
        log.trace { "Applying tenantId=${authInfo()?.tenantId} to method ${Thread.currentThread().stackTrace[4].methodName}" }

    private fun Query.withTenant(entityClass: Class<*>? = null, collectionName: String? = null): Query {
        if (entityClass == null && collectionName == null)
            throw java.lang.IllegalArgumentException("entityClass or collectionName must be supplied")

        if (isEntityTenantAware(entityClass, collectionName)) {
            val orgId = authInfo()?.tenantId ?: return this
            this.addCriteria(Criteria.where("tenantId").`is`(orgId))
        }

        return this
    }

    private fun Document.withTenant(entityClass: Class<*>? = null, collectionName: String? = null): Document {
        if (entityClass == null && collectionName == null)
            throw java.lang.IllegalArgumentException("entityClass or collectionName must be supplied")

        if (isEntityTenantAware(entityClass, collectionName)) {
            val orgId = authInfo()?.tenantId ?: return this
            return when (this::class.simpleName) {
                "EmptyDocument" -> Query.query(Criteria.where("tenantId").`is`(orgId)).queryObject
                else -> this.also { it.append("tenantId", orgId) }
            }
        }

        return this
    }

    private fun Aggregation.withTenant(collectionName: String? = null): Aggregation {
        if (collectionName == null)
            throw java.lang.IllegalArgumentException("entityClass or collectionName must be supplied")

        if (isEntityTenantAware(null, collectionName)) {
            val orgId = authInfo()?.tenantId ?: return this
            val operations = this.pipeline.operations
            val options = this.options
            val match = operations.find { it.operator == "\$match" }
            val restOperations = operations.filter { it != match }

            return if (match == null) {
                val tenantMatch = Aggregation.match(Criteria.where("tenantId").`is`(orgId))
                Aggregation.newAggregation(listOf(tenantMatch) + restOperations).withOptions(options)
            } else {
                val existingCriteria = getField(match as MatchOperation, "criteriaDefinition") as Criteria
                val newMatch =
                    Aggregation.match(Criteria().andOperator(Criteria.where("tenantId").`is`(orgId), existingCriteria))
                Aggregation.newAggregation(listOf(newMatch) + restOperations).withOptions(options)
            }
        }

        return this
    }

    private fun getField(operation: MatchOperation, fieldName: String): Any? {
        val criteriaField = operation::class.java.getDeclaredField(fieldName)
        ReflectionUtils.makeAccessible(criteriaField)
        return ReflectionUtils.getField(criteriaField, operation)
    }

    private fun isEntityTenantAware(entityClass: Class<*>? = null, collectionName: String? = null): Boolean {
        if (entityClass?.kotlin?.isSubclassOf(TenantAwareEntity::class) == true) {
            return true
        }

        val type = mongoConverter.mappingContext.persistentEntities.find { it.collection == collectionName }?.type
        if (type?.kotlin?.isSubclassOf(TenantAwareEntity::class) == true) {
            return true
        }

        return false
    }
}
