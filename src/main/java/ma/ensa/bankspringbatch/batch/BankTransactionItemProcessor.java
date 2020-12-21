package ma.ensa.bankspringbatch.batch;

import ma.ensa.bankspringbatch.entities.BankTransaction;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;

//@Component    //on enleve l'annotation car en va faire l'instanciation du bean dans la classe de config
public class BankTransactionItemProcessor implements ItemProcessor<BankTransaction, BankTransaction> {

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy-HH:mm");

    @Override
    public BankTransaction process(BankTransaction bankTransaction) throws Exception {
        bankTransaction.setTransactionDate(dateFormat.parse(bankTransaction.getStrTransactionDate()));
        return bankTransaction;
    }

    /*On a 2 types de processors :
    * --> De type Stateless : c-à-d il contient que la methode process() qui fait des traintements en prenant
    *                          un object Input et le transformer pour donner un Object en Output
    * --> De type Avec Etat : c-à-d il contient des variable en plus de la methode process(),
    *                          alors il fait le traitement et le calcule et il garde les données pour
    *             les utiliser dans l'application ou les visualiser en temps réel au moment de l'execution.
    * */
}
