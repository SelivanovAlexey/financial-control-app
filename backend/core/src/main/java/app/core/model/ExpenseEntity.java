package app.core.model;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "expenses", schema = "public")
@SequenceGenerator(name = "transaction_seq", sequenceName = "expenses_seq", allocationSize = 1)
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
public class ExpenseEntity extends TransactionBaseEntity {
}
