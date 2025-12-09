package app.core.model;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "expenses", schema = "public")
@SequenceGenerator(name = "transaction_seq", sequenceName = "expenses_seq", allocationSize = 1)
public class ExpenseEntity extends TransactionBaseEntity {

}
