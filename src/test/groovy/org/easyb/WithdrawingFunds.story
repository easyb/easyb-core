scenario "withdrawing funds", {
    given "an account with a \$100 balance"
    when "an account holder withdraws \$75"
    then "the account balance should be \$25"
}