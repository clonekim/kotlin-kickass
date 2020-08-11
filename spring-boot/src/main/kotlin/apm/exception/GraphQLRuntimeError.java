package apm.exception;

import graphql.ErrorType;
import graphql.GraphQLError;
import graphql.language.SourceLocation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class GraphQLRuntimeError extends RuntimeException implements GraphQLError {

    public GraphQLRuntimeError() {
    }

    public GraphQLRuntimeError(String msg) {
        super(msg);
    }

    public GraphQLRuntimeError(String msg, Throwable throwable) {
        super(msg, throwable);
    }


    @Override
    public List<SourceLocation> getLocations() {
        return null;
    }

    @Override
    public ErrorType getErrorType() {
        return null;
    }

    @Override
    public Map<String, Object> getExtensions() {
        Map<String, Object> errorsMap = new HashMap<>();
        errorsMap.put("errorType", this.getClass().getSimpleName());
        errorsMap.put("errorStatus", getStatus());
        return errorsMap;
    }

    abstract public int getStatus();
}
