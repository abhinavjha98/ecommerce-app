<?php

include_once 'braintree-php-3.34.0/lib/Braintree.php';

if(isset($_POST["environment"])&&($_POST["merchantId"]) &&($_POST["publicKey"]) &&($_POST["privateKey"])){
    

    Braintree_Configuration::environment(trim($_POST["environment"]));
    Braintree_Configuration::merchantId(trim($_POST["merchantId"]));
    Braintree_Configuration::publicKey(trim($_POST["publicKey"]));
    Braintree_Configuration::privateKey(trim($_POST["privateKey"]));

    if(isset($_POST["NONCE"])&&($_POST["amount"])){
        $nonceFromTheClient = $_POST["NONCE"];
        $amountFromClient = $_POST["amount"];
        $result = Braintree_Transaction::sale([
            'amount' => $amountFromClient,
            'paymentMethodNonce' => $nonceFromTheClient,
            'options' => ['submitForSettlement' => true ]
        ]);


        if ($result->success) {
            //echo("success!: " . $result->transaction->id);
            echo $result->transaction->paymentInstrumentType;
        } else if ($result->transaction) {
            echo "Error processing transaction: \n" . " code: " . $result->transaction->processorResponseCode . " \n " . " text: " . $result->transaction->processorResponseText;
        } else {
            echo "Validation errors: \n"; //+ $result->errors->deepAll();
        }

    }else{
        echo($clientToken = Braintree_ClientToken::generate());
    }

}else echo "Invalid Parameter";