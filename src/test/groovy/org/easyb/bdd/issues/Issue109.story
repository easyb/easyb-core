scenario "Non-Gold level customer with \$100 or more" , {
 given "a non-Gold level customer"
 when "they have \$100 or more in their shopping cart"
 then "they should receive a \$10 discount"
 and "they should be emailed a coupon"
}

scenario "another Non-Gold level customer with \$100 or more" , {
 given "another non-Gold level customer"
 when "they have another \$100 or more in their shopping cart"
 then "they should receive another \$10 discount"
 then "they should be emailed another coupon"
}