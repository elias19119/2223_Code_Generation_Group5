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
    public CreateTransactionResponse makeTransaction(@RequestBody CreateTransactionDTO transaction) throws Exception {
        String token = request.getHeader("Authorization");
        return transactionService.makeTransaction(transaction, token);

    }

    @GetMapping("/{IBAN}")
    public ResponseEntity<Iterable<Transaction>> getTransactionsByIBAN(@PathVariable("IBAN") String IBAN, @RequestParam(value = "ReceiverIBAN", required = false) String ReceiverIBAN,
                                                                       @RequestParam( value = "to" , required = false) LocalDateTime to, @RequestParam(value = "from", required = false) LocalDateTime from,
                                                                       @RequestParam(value = "amount", required = false) Long amount, @RequestParam(value = "offset", required = false) Integer offset,
                                                                       @RequestParam(value = "limit", required = false) Integer limit) throws Exception {
        List<Transaction> filteredTransactions = new ArrayList<>();
        String accept = request.getHeader("Accept");
            //get transactions by IBAN by default
        try {
            filteredTransactions.addAll(transactionService.findTransactionByIban(IBAN));
            if (accept != null) {
                if(request.getParameter("offset") != null && request.getParameter("limit") != null){
                    filteredTransactions.addAll(transactionService.getTransactionByLimitAndOffset(offset, limit));
                }
                if(amount != null && amount.longValue() > 0)
                {
                    if(transactionService.transferAmountEquals(IBAN,amount).isEmpty()){
                        throw new ApiRequestException("no transactions were found",HttpStatus.NOT_FOUND);
                    }
                    filteredTransactions.addAll(transactionService.transferAmountEquals(IBAN,amount));
                }
                if(request.getParameter("from") != null && request.getParameter("to") != null ){
                    filteredTransactions.addAll(transactionService.findByIbanFromAndToDate(IBAN,to,from));
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
