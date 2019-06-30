javascript:
	var tel=$("input[type='tel']");
	var password=$("input[type='password']");
	if(tel.length>0&&password.length>0){
		$("button[type='button']").click();	
	}else {
		var btnclose=$("button[class='button button-block button-positive']");
		if(btnclose.length>0)
			btnclose.click();
	}
	
	var select=$(".btns .title");
	if(select.length>0){
		setTimeout(function(){
			$(select[0]).click();	
		},1000);
	}
	var time1 = setInterval(function () {
		var start=$(".normal .mc-btn");
		if(start.length>0)
			start.click();	     
	  },10000);

	var time2 = setInterval(function () {
		var restart=$(".connect-btn");
		if(restart.length==2)
			$(restart[0]).click();
	 },10000);
	var time3 = setInterval(function () {
		var received=$(".connected");
		if(received.length>0){
			interception.customReceived();
			clearInterval(time1);
			clearInterval(time2);
			clearInterval(time3);
		}		
	 },10000);