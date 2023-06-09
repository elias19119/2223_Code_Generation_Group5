package io.swagger.Controller;

import io.swagger.Service.TransactionService;
import io.swagger.exceptions.ApiRequestException;
import io.swagger.model.DTOs.CreateTransactionDTO;
import io.swagger.model.DTOs.DepositToCheckingAccountDTO;
import io.swagger.model.DTOs.WithdrawMoneyDTO;
import io.swagger.model.Responses.CreateTransactionResponseDTO;
import io.swagger.model.Responses.WithdrawMoneyResponseDTO;
import io.swagger.model.Transaction;
import lombok.AllArgsConstructor;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@AllArgsConstructor
@RestController
@RequestMapping("/transactions")
@CrossOrigin("*")

public class TransactionController {

  private final TransactionService transactionService;
    private final HttpServletRequest request;

    @PostMapping
    @PreAuthorize("hasAnyRole('ROLE_CUSTOMER','ROLE_EMPLOYEE')")
    public ResponseEntity<CreateTransactionResponseDTO> makeTransaction(@RequestBody CreateTransactionDTO transaction) throws Exception {
        try{
            return new ResponseEntity<>(HttpStatus.CREATED).status(201).body(transactionService.makeTransaction(transaction));
        }catch (Exception e){
            throw  new ApiRequestException(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{IBAN}")
    @PreAuthorize("hasAnyRole('ROLE_CUSTOMER','ROLE_EMPLOYEE')")
    public ResponseEntity<Iterable<Transaction>> getTransactionsByIBAN(@PathVariable("IBAN") String senderIBAN, @RequestParam(value = "ReceiverIBAN", required = false) String receiverIBAN,
                                                                       @RequestParam( value = "to" , required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to, @RequestParam(value = "from", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
                                                                       @RequestParam(value = "amount", required = false) Long amount, @RequestParam(value = "offset", required = false) Integer offset,
                                                                       @RequestParam(value = "limit", required = false) Integer limit) throws Exception {
        List<Transaction> filteredTransactions = new ArrayList<>();
        try {
            filteredTransactions =  transactionService.findTransactionsByFilters(senderIBAN,receiverIBAN,amount,to,from,offset,limit);
             return new ResponseEntity<Iterable<Transaction>>(HttpStatus.ACCEPTED).status(200).body(filteredTransactions);


        }catch (Exception e){
            throw new ApiRequestException(e.getMessage(),HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("")
    @PreAuthorize("hasAnyRole('ROLE_CUSTOMER','ROLE_EMPLOYEE')")
    public ResponseEntity<Iterable<Transaction>> getAllTransactions() throws Exception {
        try {

            return new ResponseEntity<Iterable<Transaction>>(HttpStatus.ACCEPTED).status(200).body(transactionService.findAllTransactions());


        }catch (Exception e){
            throw new ApiRequestException(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PostMapping("/deposit")
    @PreAuthorize("hasAnyRole('ROLE_CUSTOMER','ROLE_EMPLOYEE')")
    public ResponseEntity<String> deposit(@RequestBody DepositToCheckingAccountDTO depositToCheckingAccountDTO) throws Exception {
        try{
            return new ResponseEntity<String>(HttpStatus.OK).status(200).body(transactionService.depositMoney(depositToCheckingAccountDTO));
        }catch (Exception e){
            throw new ApiRequestException(e.getMessage(),HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/withdraw")
    @PreAuthorize("hasAnyRole('ROLE_CUSTOMER','ROLE_EMPLOYEE')")
    public ResponseEntity<WithdrawMoneyResponseDTO> withdraw(@RequestBody WithdrawMoneyDTO withdrawMoneyDTO) throws Exception {
        try {
            return new ResponseEntity<WithdrawMoneyResponseDTO>(HttpStatus.ACCEPTED).status(200).body(transactionService.withdrawMoney(withdrawMoneyDTO));
        }catch (Exception e){
            throw new ApiRequestException(e.getMessage(),HttpStatus.BAD_REQUEST);

        }
    }

}
