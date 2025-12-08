package app.core.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

//TODO: валидация полей
@Getter
@Setter
@MappedSuperclass
public abstract class TransactionBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "transaction_seq")
    protected Long id;

    @Column(name = "amount", nullable = false)
    protected Long amount;

    @Column(name = "category", length = 128, nullable = false)
    protected String category;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    @Column(name = "create_date", nullable = false)
    protected OffsetDateTime createDate;

    @Column(name = "description")
    protected String description;

    @ManyToOne
    @JsonIgnore
    protected User user;
}
