#include <emp-sh2pc/emp-sh2pc.h>
#include <map>

using namespace emp;
using namespace std;




namespace Millionaires {



class MillionairesClass {


  string aliceHost = "127.0.0.1";
  NetIO *io = 0; // null it out
  int party;
  int port;



public:
   void configure(int aParty, int aPort, string aHost) {

	   aliceHost = aHost;
	   port = aPort;
	   party = aParty;

	   std::cout << "starting run in emp! party=" << party << " port=" << port <<  " alice host=" << aliceHost <<  std::endl;

	   io = new NetIO((party==ALICE ? nullptr : aliceHost.c_str()), port);
	   setup_semi_honest(io, party);

   }

	bool run(int netWorth) {


	    //bool *inputBits = prepareInput(netWorth);

	    Integer aliceInput = Integer(sizeof(netWorth)*8, party == ALICE ? netWorth : 0, ALICE);
	    Integer bobInput = Integer(sizeof(netWorth)*8, party == BOB ? netWorth : 0, BOB);

	    Bit result = aliceInput > bobInput;
		return result.reveal<bool>(PUBLIC);

	}

	void cleanup() {
		delete io;
	}

	}; // end class
} // end namespace
