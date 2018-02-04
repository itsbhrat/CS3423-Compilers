B
class Main inherits IO {
    a : String <- "THIS IS A VALID STRING";
    b : String <- "THIS IS A VALID\
                   STRING ";
                   
    e : String <- "\\\\\w";
    c : String <- "In This\t \n valid string \b STR_CONST\
                   replaces escape sequences by their correct values\
                   A \" quote is also included for the test case\f";
                   
    d : String <- "In This valid string the e\scape ch\aract\ers that are not special are re\place\d by the\ cha\ra\cter. ex \c becomes c . All the other escape chara\ct\ers i\n this \string \are als\o repl\aced\"";
    
    e : String <- "Invalid String 
    coz of new line";
    
    longerString : String <- "QmWgAu9pMyIAmw4HDteUhEfyh5ZH54nkEFEucOmWJhBQF0Ca8ix6n4Oi8BD4nih2t9wglW9O93DO9kFo8O9gp4X9GtssnOLmmY9SoBQ4RraRDlJijxifyEfvQSyxtWFknKvI0Y2eUWwlqZuOyM4ZVCINiwuZzRWHIXJ3I35L5XHXmMa17WBQlKAmDWIQXRY7KflceoXDJItE0gt2HRzoM0SKS8f6E7uYYStaXrUIr2ZDAgTbTI6B6CBSbsXmz4xrIf7yfkXtYbJfAiu6TRSVCrJocqwZfSafwXINUc2lQaQmSHf3E68YbZjBohLT2bPmCEi616Q7KglT64fWe5etyo6nOs613F8KKA2zKD6YSV8oyR4OlFOUursRDR1wirrg3boA0J24fhN8QIPyMYXOALfArPf4nQ5Cn20ZqDfr7uCuapVaxgDARBg6THy2fYqVNiIy3FYJ03gQLaAShrFhOqtoLk882pphqN8WaSZxYbo3Rq4rK9ykixglJ52lF5OHaVOiZm6Qg0g3a0FxFv4IFKGAlbXsjrlyasDUP3j2DX5f8Mbjc1VI2ROpAOklb9KC0ecKB0CDh1K2LuWhFJbs04mwlRh9btT7DK0ceWbhPwLAx0DlaRQmf78Iyy1t5MUy2Dg80yqU6Nk0rqJrIQbbUr9o6CEL364QYJMgHbzmEg4pmC8Db0mlMFckoyLAkiEgjZgWbCLr5MnhVX6R2O3nUfmmerA6CxytoqQF6ctopHZ7klwGlohy02X88SUvWxwAPqHDMuZseQvBaQODXEUT4E21pvVmw4tWP3n0rbU2tPyhL3KWagqJHQaHySAfLR24pqE2UXBwjWkWt8i3GF4xweV0CBEBY6uv3WJKEOFrHNVxt8zivPlkvW0bpK8vf5G0kAWbI1WWSwCCurNxC6E22Kto050ZV0q0rYInjaxXUZtHOpnATfObq8Rm01KV8v2DvtsQawqEmPPGcwtLGMcUwypXfE5FrfDUJ3WUKZafyByQBNpODjyf4tDq23U63VN3462DzyGiDcmtmpMQqj9RFQoIJu"
	main():IO {

	};
};

"unterminated string constant
