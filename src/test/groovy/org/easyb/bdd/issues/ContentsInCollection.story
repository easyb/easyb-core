
scenario "easyb should find contents in a collection", {

  given "a collection of strings", {
    uris = ["http://acme.com/w018/widget/4000000001/200901/AQSN_ESTB/PREM/ORIG/1000",
              "http://acme.com/w018/widget/4000000001/200901/PFP_CSLDN_XFER/PREM/ORIG/1000",
              "http://acme.com/w018/widget/4000000001/200901/PFP_CSLDN_RCPTR/PREM/ORIG/1000",
              "http://acme.com/w018/widget/4000000001/200901/AMRT_AMRT_AM_RR/PREM/ACCD_AMRT/10"]

  }

  then "easyb should find items in it" , {
    uris.shouldHave "http://acme.com/w018/widget/4000000001/200901/AQSN_ESTB/PREM/ORIG/1000"
  }

}