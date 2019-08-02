javascript:
socket=new WebSocket("ws://211.159.185.14:9877/Task?UserID=19297&TaskPrice=&DownTaskPoint=0&TaskCategory=0&token=5adc3dd6857bfc095dc92054f5fbb457");
socket.onopen=function(event){
	test.open("open");
};
socket.onclose=function(event){
	test.close("close");
};
socket.onmessage=function(event){
	test.message(event.data);
};