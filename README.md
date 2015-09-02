# me.mishok13/syslog

[![Build Status](https://travis-ci.org/mishok13/syslog.svg?branch=master)](https://travis-ci.org/mishok13/syslog)
[![Dependencies Status](http://jarkeeper.com/mishok13/syslog/status.png)](http://jarkeeper.com/mishok13/syslog)
[![License](https://img.shields.io/badge/license-MIT-lightgray.svg)](http://opensource.org/licenses/MIT)

Pure Clojure implementation of
[RFC 5424, syslog protocol](https://tools.ietf.org/html/rfc5424) and
other syslog-related RFCs.

## Status

Here's the breakdown on the implementation of message formatting (section 6 of RFC 5424)

- [ ] Message length
- [ ] Header
- [ ] Structured data
- [x] Message
- [ ] Examples (ideally all examples presented in section 6.5 should
  be present in the unit tests)

Sections 8 and 9 are still in works. Section 7 is implemented not
according to standard and will be rewritten.


## License

Copyright © 2015 Andrii V. Mishkovskyi

Distributed under the MIT license.
