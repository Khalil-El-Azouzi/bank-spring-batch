package ma.ensa.bankspringbatch.dao;

import ma.ensa.bankspringbatch.entities.BankTransaction;
import org.springframework.data.jpa.repository.JpaRepository;


public interface BankTransactionRepository extends JpaRepository<BankTransaction,Long> {
}
