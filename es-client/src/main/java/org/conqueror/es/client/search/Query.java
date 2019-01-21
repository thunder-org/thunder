package org.conqueror.es.client.search;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;

import java.io.Serializable;
import java.util.Date;


/*
 * variable queries for the ElasticSearch
 * match query : 입력 string을 analyze해서 query
 * multimatch query : multiple fields에 query
 * boolean query : 여러 queries 조합
 * boosting : 특정 field의 특정 value를 가진 documents의 score를 낮춤
 * ids : 특정 ids를 가진 documents 조회
 * constant score : 특정 query나 filter에 부합하는 documents에 특정 score를 부여
 * disjunction max query : multiple fields에 검색하고 각각 다른 boost factor를 가진 경우
 * fuzzy query :
 * fuzzy like this query :
 * fuzzy like this field query :
 * has child
 * has parent
 * matchall
 * more like this
 * more like this field
 * prefix
 * querystring
 * range
 * span first
 * span near
 * span not
 * span or
 * span term
 * term
 * terms
 * top children
 * wildcard
 * nested
 * indices
 * geoshape
 *
 */
public class Query implements Serializable {

    public enum BoolType {MUST, MUST_NOT, SHOULD}

    public enum RangeType {LT, LTE, GT, GTE}

    private BoolQueryBuilder query = new BoolQueryBuilder();

    public Query addTermQuery(String fieldName, Object value, BoolType bType) {
        return addQuery(QueryBuilders.termQuery(fieldName, value), bType);
    }

    public Query addTermQuery(String fieldName, String value, BoolType bType) {
        return addQuery(QueryBuilders.termQuery(fieldName, value), bType);
    }

    public Query addTermQuery(String fieldName, int value, BoolType bType) {
        return addQuery(QueryBuilders.termQuery(fieldName, value), bType);
    }

    public Query addTermQuery(String fieldName, long value, BoolType bType) {
        return addQuery(QueryBuilders.termQuery(fieldName, value), bType);
    }

    public Query addTermQuery(String fieldName, float value, BoolType bType) {
        return addQuery(QueryBuilders.termQuery(fieldName, value), bType);
    }

    public Query addTermQuery(String fieldName, double value, BoolType bType) {
        return addQuery(QueryBuilders.termQuery(fieldName, value), bType);
    }

    public Query addTermQuery(String fieldName, boolean value, BoolType bType) {
        return addQuery(QueryBuilders.termQuery(fieldName, value), bType);
    }

    public Query addRangeQuery(String fieldName, long value, RangeType rType, BoolType bType) {
        return addRangeQuery(fieldName, (Object) value, rType, bType);
    }

    public Query addRangeQuery(String fieldName, float value, RangeType rType, BoolType bType) {
        return addRangeQuery(fieldName, (Object) value, rType, bType);
    }

    public Query addRangeQuery(String fieldName, double value, RangeType rType, BoolType bType) {
        return addRangeQuery(fieldName, (Object) value, rType, bType);
    }

    public Query addRangeQuery(String fieldName, Date value, RangeType rType, BoolType bType) {
        return addRangeQuery(fieldName, (Object) value, rType, bType);
    }

    public Query addRangeQuery(String fieldName, long from, long to, boolean include, BoolType bType) {
        return addRangeQuery(fieldName
            , from
            , (include ? RangeType.GTE : RangeType.GT)
            , to
            , (include ? RangeType.LTE : RangeType.LT)
            , bType);
    }

    public Query addRangeQuery(String fieldName, float from, float to, boolean include, BoolType bType) {
        return addRangeQuery(fieldName
            , from
            , (include ? RangeType.GTE : RangeType.GT)
            , to
            , (include ? RangeType.LTE : RangeType.LT)
            , bType);
    }

    public Query addRangeQuery(String fieldName, double from, double to, boolean include, BoolType bType) {
        return addRangeQuery(fieldName
            , from
            , (include ? RangeType.GTE : RangeType.GT)
            , to
            , (include ? RangeType.LTE : RangeType.LT)
            , bType);
    }

    public Query addRangeQuery(String fieldName, Date from, Date to, boolean include, BoolType bType) {
        return addRangeQuery(fieldName
            , from
            , (include ? RangeType.GTE : RangeType.GT)
            , to
            , (include ? RangeType.LTE : RangeType.LT)
            , bType);
    }

    public QueryBuilder getQueryBuilder() {
        return this.query;
    }

    public String toJsonQuery() {
        return this.query.toString();
    }

    private Query addRangeQuery(String fieldName, Object value, RangeType rType, BoolType bType) {
        RangeQueryBuilder query = QueryBuilders.rangeQuery(fieldName);

        switch (rType) {
            case LT:
                query.lt(value);
                break;
            case LTE:
                query.lte(value);
                break;
            case GT:
                query.gt(value);
                break;
            case GTE:
                query.gte(value);
                break;
        }

        return addQuery(query, bType);
    }

    private Query addRangeQuery(String fieldName, Object from, RangeType fromRType, Object to, RangeType toRType, BoolType bType) {
        RangeQueryBuilder query = QueryBuilders.rangeQuery(fieldName);

        switch (fromRType) {
            case LT:
                query.lt(from);
                break;
            case LTE:
                query.lte(from);
                break;
            case GT:
                query.gt(from);
                break;
            case GTE:
                query.gte(from);
                break;
        }
        switch (toRType) {
            case LT:
                query.lt(to);
                break;
            case LTE:
                query.lte(to);
                break;
            case GT:
                query.gt(to);
                break;
            case GTE:
                query.gte(to);
                break;
        }

        return addQuery(query, bType);
    }

    private Query addQuery(QueryBuilder query, BoolType bType) {
        switch (bType) {
            case MUST:
                this.query.must(query);
                break;
            case MUST_NOT:
                this.query.mustNot(query);
                break;
            case SHOULD:
                this.query.should(query);
                this.query.minimumShouldMatch(1);
                break;
        }

        return this;
    }

}
