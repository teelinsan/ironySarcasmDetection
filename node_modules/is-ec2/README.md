# is-ec2

Sometimes you need to know if you are running on EC2 or not. This will tell you.

~~~sh
npm install is-ec2
~~~

~~~js
var isec2 = require('is-ec2');

if (isec2()) {
  console.log('yep');
}
~~~

This works by executing the following command:

~~~sh
if [ -f /sys/hypervisor/uuid ]; then echo `head -c 3 /sys/hypervisor/uuid`; fi
~~~

Only work on Linux EC2 instances, not Windows. See this [StackOverflow article](http://serverfault.com/questions/462903/how-to-know-if-a-machine-is-an-ec2-instance) for details.
