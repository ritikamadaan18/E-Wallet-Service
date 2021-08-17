package com.gfg.jbdl12majorproject.wallet.resource;



import com.gfg.jbdl12majorproject.ForbiddenException;
import com.gfg.jbdl12majorproject.wallet.manager.WalletManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class WalletController {
    @Autowired
    WalletManager walletManager;


    @PutMapping("/addAmount/{user_id}/{amount}")
    ResponseEntity addAmount(@PathVariable("user_id") String userId,
                             @PathVariable("amount") Double amount, Authentication authentication){
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken
                = new UsernamePasswordAuthenticationToken(authentication.getPrincipal(),
                authentication.getCredentials(),authentication.getAuthorities());
        try{
            walletManager.addAmount(userId,amount,usernamePasswordAuthenticationToken);
            return ResponseEntity.ok().build();
        }catch(ForbiddenException ex){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
        catch (Exception exception) {
            log.error(exception.getMessage());
            return ResponseEntity.badRequest().build();
        }


    }


}
