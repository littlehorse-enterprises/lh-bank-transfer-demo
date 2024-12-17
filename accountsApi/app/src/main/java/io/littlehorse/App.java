/*
 * This source file was generated by the Gradle 'init' task
 */
package io.littlehorse;

import io.javalin.Javalin;
import java.util.Random;
import java.util.UUID;
import org.json.JSONObject;

public class App {

    public static void main(String[] args) {
        // create the accountstore and transfer store
        accountStore acctStore = new accountStore();
        transferStore transferStore = new transferStore();
        // TODO: fix this...it's hacky, but works
        // load the test data
        acctStore.loadTestData();

        // Start Javalin server
        Javalin app = Javalin.create().start(7070);

        // Define a simple GET route - This is for livelyness tests
        app.get("/hello", ctx -> {
            ctx.result("Hello, " + ctx.host());
        });

        app.get("/allAccounts", ctx -> {
            ctx.result(acctStore.toString());
        });
        app.get("/account/{id}", ctx -> {
            // Failure this for demo purposes 1/5th of the time.
            // The exception is handled in the workflow code .
            Random random = new Random();
            if (random.nextInt(0, 5) == 0) {
                ctx.status(500);
                ctx.result("Internal Server Error");
            }
            if (acctStore.queryByField("accountId", ctx.pathParam("id")) != null) {
                ctx.result(
                        acctStore.queryByField("accountId", ctx.pathParam("id")).toString());
            }
        });

        app.post("/transfer", ctx -> {
            JSONObject transferInfo = new JSONObject(ctx.body());
            if (transferInfo.has("fromAccountId")
                    && transferInfo.has("toAccountId")
                    && transferInfo.has("amount")
                    && transferInfo.has("description")
                    && transferInfo.has("currency")) {
                UUID result = transferStore.addTransaction(transferInfo);
                ctx.result("{\n" + "\"transferId\": \"" + result.toString() + "\"\n}");
            } else {
                ctx.result("Missing fields");
            }
        });

        app.get("/transferStatus/{transferId}", ctx -> {
            String result = transferStore.getTransaction(UUID.fromString(ctx.pathParam("transferId")));
            JSONObject ret = new JSONObject(result);
            if (ret.has("completedAt")) {
                ret.remove("completedAt");
            }
            ret.remove("completeTime");
            ctx.result(ret.toString());
        });

        // respond with a JSONDocument of currency conversions
        app.get("/currency/{currency}", ctx -> {
            ctx.result("currency api - not implemented");
        });

        // this is only for testing
        app.get("/allTransfers", ctx -> {
            ctx.result(transferStore.getAllTransactions().toString());
        });
    }

    // Define a simple model class to represent the JSON structure
    public static class Account {
        //        {
        //        "accountId": 123,
        //        "name": "mitch",
        //        "accountBalance": "123.23",
        //        "accountStatus": "ACTIVE",
        //        "accountType": "SAVINGS"
        //        }
        private Integer accountId;
        private String name;
        private Double accountBalance;
        private String accountStatus; // ACTIVE or NOT
        private String accountType; //

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Integer getaccountId(Integer accountId) {
            return accountId;
        }

        public void setAccountId(Integer accountId) {
            this.accountId = accountId;
        }

        public Double getAccountBalance(Double accountBalance) {
            return accountBalance;
        }

        public void setAccountBalance(Double accountBalance) {
            this.accountBalance = accountBalance;
        }

        public String getAccountStatus(String accountStatus) {
            return accountStatus;
        }

        public void setAccountStatus(String accountStatus) {
            this.accountStatus = accountStatus;
        }

        public String getAccountType(String accountType) {
            return accountType;
        }

        public void setAccountType(String accountType) {
            this.accountType = accountType;
        }
    }
}
