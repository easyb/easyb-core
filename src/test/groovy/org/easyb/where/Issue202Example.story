scenario "strange txt report with a clause where having 2 variables (defect or misuse ?)",{
where "2 variables having 2 values", [x:[1,2], y:[1,2]]

 when "i do anything, variables being x=#x and y=#y", {
   println "${x} ${y}"
 }
 then "shouldn't easyb report 2 scenarios ? instead of reporting 4 scenarios including 2 ignored ones ?", {
         x.shouldEqual y
 }

}
