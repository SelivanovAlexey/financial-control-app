package app.core.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "incomes", schema = "public")
@SequenceGenerator(name = "transaction_seq", sequenceName = "incomes_seq", allocationSize = 1)
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
public class IncomeEntity extends TransactionBaseEntity {
}
