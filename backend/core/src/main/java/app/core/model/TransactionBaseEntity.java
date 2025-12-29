package app.core.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.OffsetDateTime;

@Getter
@Setter
@MappedSuperclass
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
public abstract class TransactionBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "transaction_seq")
    protected Long id;

    @Column(name = "amount", nullable = false)
    protected Long amount;

    @Column(name = "category", length = 128, nullable = false)
    protected String category;

    @Column(name = "create_date", nullable = false)
    protected OffsetDateTime createDate;

    @Column(name = "description")
    protected String description;

    @ManyToOne
    protected UserEntity user;
}
