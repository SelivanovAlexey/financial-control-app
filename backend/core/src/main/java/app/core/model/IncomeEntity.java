package app.core.model;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "incomes", schema = "public")
@SequenceGenerator(name = "transaction_seq", sequenceName = "incomes_seq", allocationSize = 1)
public class IncomeEntity extends TransactionBaseEntity {
}
