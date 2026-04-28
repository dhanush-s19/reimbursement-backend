package com.reimbursement.backend.migration;

import com.reimbursement.backend.model.Permission;
import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.ArrayList;
import java.util.List;

@ChangeUnit(id="init-permisions",order="001",author="you")
public class PermissionMigration {
    @Execution
    public void execute(MongoTemplate mongoTemplate) {

        List<Permission> permissions = List.of(
                new Permission(null, "CREATE_REIMBURSEMENT"),
                new Permission(null, "EDIT_REIMBURSEMENT"),
                new Permission(null, "DELETE_REIMBURSEMENT"),
                new Permission(null, "APPROVE_REIMBURSEMENT"),
                new Permission(null, "REJECT_REIMBURSEMENT"),
                new Permission(null, "VIEW_ALL_REIMBURSEMENTS")
        );

        mongoTemplate.insertAll(permissions);
    }

    @RollbackExecution
    public void rollback(MongoTemplate mongoTemplate) {
        mongoTemplate.dropCollection("permissions");
    }
}
