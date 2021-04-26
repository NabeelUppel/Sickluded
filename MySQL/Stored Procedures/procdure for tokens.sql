delimiter //
create procedure clearTokens()
Begin
update users set password_reset_token=null ,token_expiration = null  where token_expiration < Now();
end;//

delimiter ;


