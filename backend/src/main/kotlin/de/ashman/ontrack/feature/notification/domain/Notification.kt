import de.ashman.ontrack.feature.user.domain.User
import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.Instant
import java.util.*

@Entity
@Table(name = "notifications")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
abstract class Notification(
    @Id
    val id: UUID = UUID.randomUUID(),

    var read: Boolean = false,

    @ManyToOne(fetch = FetchType.LAZY)
    val sender: User,

    @ManyToOne(fetch = FetchType.LAZY)
    val receiver: User,

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    var createdAt: Instant = Instant.now(),

    @UpdateTimestamp
    @Column(name = "updated_at")
    var updatedAt: Instant = Instant.now(),
)
