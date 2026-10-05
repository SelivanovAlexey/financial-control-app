package app.core.model;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "expenses")
@SequenceGenerator(name = "transaction_seq", sequenceName = "expenses_seq", allocationSize = 1)
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
public class ExpenseEntity extends TransactionBaseEntity {
}
