package ma.ensa.bankspringbatch.web;

import ma.ensa.bankspringbatch.batch.BankTransactionItemAnalyticsProcessor;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class BankRestController {

    @Autowired
     private JobLauncher jobLauncher;// permet de lancer notre job
    @Autowired
    private Job job;
    //on peut injecter par constructeur ou par @Autowired
    @Autowired
    private BankTransactionItemAnalyticsProcessor itemAnalyticsProcessor;

    /*
    * C'est un lancement de Job parametrer avec une API REST
    * */
    @GetMapping("/startJob")
    public BatchStatus load() throws JobParametersInvalidException, JobExecutionAlreadyRunningException,
            JobRestartException, JobInstanceAlreadyCompleteException {
        //on doit en 1ier parametrer notre Job par un Map de JobParameter
        Map<String, JobParameter> parametersMap = new HashMap<>();
        parametersMap.put("time",new JobParameter(System.currentTimeMillis()));//içi on a utiliser un seul parametre time
        JobParameters jobParameters = new JobParameters(parametersMap);
        JobExecution jobExecution = jobLauncher.run(job,jobParameters);//Creation d'un JoBExecution
        while (jobExecution.isRunning()){// on a utiliser le while car le JobExecution s'execute dans un Thread
            System.out.println("Job is running ...");
        }
        return jobExecution.getStatus();

    }

    @GetMapping("/analytics")
    public Map<String, Double> analytics(){
        Map<String, Double> map = new HashMap<>();
        map.put("Somme Credits", itemAnalyticsProcessor.getTotalCredit());
        map.put("Somme Debits", itemAnalyticsProcessor.getTotalDebit());
        return map;
    }


}
