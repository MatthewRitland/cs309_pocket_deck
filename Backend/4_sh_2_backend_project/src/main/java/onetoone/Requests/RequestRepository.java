package onetoone.Requests;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

public interface RequestRepository extends JpaRepository<Request, Integer>{
    Request findById (int id);

    @Transactional
    void deleteById(int id);

    Request findByRequestedIdAndRequesterId(int requestedId, int requesterId);
}
