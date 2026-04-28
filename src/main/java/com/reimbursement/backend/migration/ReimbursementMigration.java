package com.reimbursement.backend.migration;

import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Update;

@ChangeUnit(id = "init-reimbursement-status", order = "001", author = "you")
public class ReimbursementMigration {

    @Execution
    public void execute(MongoTemplate mongoTemplate) {

        mongoTemplate.updateMulti(
                null,
                new Update()
                        .set("status", "PENDING")
                        .set("submissionCount", 0),
                "reimbursements"
        );
    }
}
