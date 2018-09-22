var exec = require("child_process").execSync;

// Inspiration taken from http://serverfault.com/questions/462903/how-to-know-if-a-machine-is-an-ec2-instance
var command =
  "if [ -f /sys/hypervisor/uuid ]; then echo `head -c 3 /sys/hypervisor/uuid`; fi";

// This is all of it.
module.exports = function() {
  try {
    return (
      exec(command, { stdio: ["inherit", "inherit", "ignore"] })
        .toString()
        .trim() === "ec2"
    );
  } catch (err) {
    return false;
  }
};
