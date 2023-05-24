package io.swagger.Controller;

import io.swagger.Security.JwtTokenProvider;
import io.swagger.Service.TransactionService;
import io.swagger.exceptions.ApiRequestException;
import io.swagger.model.DTOs.CreateTransactionDTO;
import io.swagger.model.DTOs.DepositToCheckingAccountDTO;
import io.swagger.model.DTOs.WithdrawMoneyDTO;
import io.swagger.model.Responses.CreateTransactionResponse;
import io.swagger.model.Responses.WithdrawMoneyResponse;
import io.swagger.model.Transaction;
import lombok.AllArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/transactions")

public class TransactionController {

  private final TransactionService transactionService;
    private final HttpServletRequest request;

    @PostMapping
    //Employee should be able to do transaction from any account
    //@PreAuthorize("hasAnyRole('ROLE_CUSTOMER','ROLE_EMPLOYEE')")
    public ResponseEntity<CreateTransactionResponse> makeTransaction(@RequestBody CreateTransactionDTO transaction) throws Exception {
        String token = request.getHeader("Authorization");
        try{
            return new ResponseEntity<>(HttpStatus.CREATED).status(201).body(transactionService.makeTransaction(transaction, token));
        }catch (Exception e){
            throw  new ApiRequestException(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{IBAN}")
    public ResponseEntity<Iterable<Transaction>> getTransactionsByIBAN(@PathVariable("IBAN") String senderIBAN, @RequestParam(value = "ReceiverIBAN", required = false) String receiverIBAN,
                                                                       @RequestParam( value = "to" , required = false) LocalDateTime to, @RequestParam(value = "from", required = false) LocalDateTime from,
                                                                       @RequestParam(value = "amount", required = false) Long amount, @RequestParam(value = "offset", required = false) Integer offset,
                                                                       @RequestParam(value = "limit", required = false) Integer limit) throws Exception {
        List<Transaction> filteredTransactions = new ArrayList<>();
        String accept = request.getHeader("Accept");
        try {
            if (accept != null) {
                if (amount != null && amount > 0) {
                    if (receiverIBAN != null) {
                        filteredTransactions.addAll(transactionService.findTransferAmountFromIBANAndToIBAN(senderIBAN, receiverIBAN, amount));
                    } else {
                        filteredTransactions.addAll(transactionService.findTransferAmountFromIBAN(senderIBAN, amount));
                    }
                }
                else
                    filteredTransactions.addAll(transactionService.findTransactionByIban(senderIBAN));


                if (from != null && to != null) {
                    filteredTransactions.addAll(transactionService.findByIbanFromAndToDate(senderIBAN, to, from));
                }

                if (offset != null && limit != null) {
                    filteredTransactions.addAll(transactionService.getTransactionByLimitAndOffset(offset, limit));
                }
                if(receiverIBAN != null){
                    filteredTransactions.addAll(transactionService.findFromIbanToIban(senderIBAN,receiverIBAN));
                }

                if (filteredTransactions.isEmpty()) {
                    throw new ApiRequestException("No transactions were found", HttpStatus.NOT_FOUND);
                }

                return new ResponseEntity<Iterable<Transaction>>(HttpStatus.ACCEPTED).status(200).body(filteredTransactions);

            }
        }catch (Exception e){
            throw new ApiRequestException(e.getMessage(),HttpStatus.NOT_FOUND);

        }
        return null;
    }


    @PostMapping("/deposit")
    public ResponseEntity<String> deposit(@RequestBody DepositToCheckingAccountDTO depositToCheckingAccountDTO) throws Exception {
        try{
            return new ResponseEntity<>(HttpStatus.OK).status(200).body(transactionService.depositMoney(depositToCheckingAccountDTO));
        }catch (Exception e){
            throw new ApiRequestException(e.getMessage(),HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/withdraw")
    public ResponseEntity<WithdrawMoneyResponse> withdraw(@RequestBody WithdrawMoneyDTO withdrawMoneyDTO) throws Exception {
        try {
            return new ResponseEntity<WithdrawMoneyResponse>(HttpStatus.ACCEPTED).status(200).body(transactionService.withdrawMoney(withdrawMoneyDTO));
        }catch (Exception e){
            throw new ApiRequestException(e.getMessage(),HttpStatus.BAD_REQUEST);

        }
    }

}
