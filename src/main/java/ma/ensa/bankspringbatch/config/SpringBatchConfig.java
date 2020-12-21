package ma.ensa.bankspringbatch.config;

import ma.ensa.bankspringbatch.batch.BankTransactionItemAnalyticsProcessor;
import ma.ensa.bankspringbatch.batch.BankTransactionItemProcessor;
import ma.ensa.bankspringbatch.entities.BankTransaction;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.support.CompositeItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.util.ArrayList;
import java.util.List;


@Configuration
@EnableBatchProcessing //catte annotation permet automatiquement la creaction de certains object(beans) en mémpoire pour les injecter facilement dans notre application
public class SpringBatchConfig {
    // pour créer un Job nous avous besion des aubjects suivant :

    @Autowired private JobBuilderFactory jobBuilderFactory;
    @Autowired private StepBuilderFactory stepBuilderFactory;
    @Autowired private ItemReader<BankTransaction> bankTransactionItemReader;
    @Autowired private ItemWriter<BankTransaction> bankTransactionItemWriter;
    //@Autowired private ItemProcessor<BankTransaction,BankTransaction> bankTransactionitemProcessor; //on l'inject dans le cas d'un seul itemProcessor

    /* Création d'un Job avec son Step */
    @Bean
    public Job bankJob(){
        Step step1 = stepBuilderFactory.get("step-data-load")
                .<BankTransaction,BankTransaction>chunk(100)//chunk est une méthode générique qui a besoin de spécifier de types Input et Output
                .reader(bankTransactionItemReader)
                //.processor(bankTransactionitemProcessor) //on cas d'un seul Itemprocessor dans l'app
                .processor(compositeItemProcessor())
                .writer(bankTransactionItemWriter)
                .build();

        return jobBuilderFactory.get("bank-data-loader-job")
                .start(step1)
                .build();
    }

    /*      La créeation des Beans ItemReader   (implementation des méthodes)   */
    @Bean
    public FlatFileItemReader<BankTransaction> fileItemReader(
            @Value("${inputFile}") Resource resource)
    // l'annotation @Value("${inputFile}") est utilisé pour injecter la valeur d'une properieter déclarer
    // dans le fichier application.properties
    {
        FlatFileItemReader<BankTransaction> flatFileItemReader = new FlatFileItemReader<>();
        flatFileItemReader.setName("CSV-READER");
        flatFileItemReader.setLinesToSkip(1);// permet de sauter la 1ier ligne du fichier qui contient just l'entete.
        flatFileItemReader.setResource(resource); //la resource qui est le fichier à traiter
        flatFileItemReader.setLineMapper(lineMapper()); // c'est une méthode qu'on configure en bas
        return  flatFileItemReader;
    }

    @Bean
    public LineMapper<BankTransaction> lineMapper() {
        DefaultLineMapper<BankTransaction>  lineMapper = new DefaultLineMapper<>();
        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        lineTokenizer.setDelimiter(",");// on féfinit le séparateur dans notre fichier data
        lineTokenizer.setStrict(false);
        lineTokenizer.setNames("id","accountID","strTransactionDate","transactionType","amount");// on féfinie l'ordre des données dans le fichier en utilisant les noms des parametres dans la classe
        lineMapper.setLineTokenizer(lineTokenizer);
        BeanWrapperFieldSetMapper<BankTransaction> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(BankTransaction.class);// on spécifie le type sible
        lineMapper.setFieldSetMapper(fieldSetMapper);
        return lineMapper;
    }
    /*  ---------------------------------------------------------------------------------------     */

    /*  Creation d'un composite pour enchainer les differents ItemProcessors dans notre application
    *   Maintenant on a 2 ItemProcessors (le 1ier fait le traitement de la date au bon format
    *                                  et le 2eme fait la somme des Credits et Debits)
    *  */
    @Bean
    public CompositeItemProcessor<BankTransaction,BankTransaction> compositeItemProcessor() {
        //création d'une liste ItemProcessors
        List<ItemProcessor<BankTransaction, BankTransaction>> itemProcessors = new ArrayList<>();
        //l'ajout des processors existant à cette liste
        itemProcessors.add(itemProcessor1());
        itemProcessors.add(itemProcessor2());
        //Création de l'object compositeItemProcessor
        CompositeItemProcessor<BankTransaction,BankTransaction> compositeItemProcessor = new CompositeItemProcessor<>();
        //déléguer la liste d'Itemprocessors au composite pour créer un Pipe d'ItemProcessors
        compositeItemProcessor.setDelegates(itemProcessors);
        //finalement en return l'object
        return compositeItemProcessor;
    }

    // si on fait l'instanciation du processor comme beans ici dans la class de config,
    // on doit enlever @Component dans la classe du processor

    @Bean
    BankTransactionItemProcessor itemProcessor1() {
        return new BankTransactionItemProcessor();
    }

    @Bean
    BankTransactionItemAnalyticsProcessor itemProcessor2() {
        return new BankTransactionItemAnalyticsProcessor();
    }
}
