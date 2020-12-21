package ma.ensa.bankspringbatch.batch;

import lombok.Getter;
import ma.ensa.bankspringbatch.entities.BankTransaction;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;

//@Component    //on enleve l'annotation car en va faire l'instanciation du bean dans la classe de config
public class BankTransactionItemAnalyticsProcessor implements ItemProcessor<BankTransaction, BankTransaction> {

    @Getter private double totalDebit;
    @Getter private double totalCredit;

    @Override
    public BankTransaction process(BankTransaction bankTransaction) {
        if(bankTransaction.getTransactionType().equals("D")) totalDebit += bankTransaction.getAmount();
        else if (bankTransaction.getTransactionType().equals("C")) totalCredit += bankTransaction.getAmount();
        return bankTransaction;
    }

    /* Maitenant on a 2 processors, Alors on va les enchainer avec le Pattern Composite
    * Donc, on va changer le beans affecter au '.processor()' dans le jobBuildeFactrory. dans la class de config
    * */
}
