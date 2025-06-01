import de.ashman.ontrack.feature.user.domain.User
import jakarta.persistence.*
import java.time.Instant
import java.util.*

@Entity
@Table(name = "notifications")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
abstract class Notification(
    @Id
    val id: UUID = UUID.randomUUID(),

    @ManyToOne(fetch = FetchType.LAZY)
    val sender: User,

    @ManyToOne(fetch = FetchType.LAZY)
    val receiver: User,

    val read: Boolean = false,

    @Column(name = "created_at")
    val createdAt: Instant = Instant.now(),
)
