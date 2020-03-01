#include <emp-sh2pc/emp-sh2pc.h>
#include <map>

using namespace emp;
using namespace std;




namespace SortByteArray {



class SortByteArrayClass {


  string aliceHost = "127.0.0.1";
  bool *inputBits; // input array of bits
  int inputLength; // in tuples
  int tupleWidth = 0; // in bits
  signed char *outputBytes;
  int unionedTupleCount = 0;



  // reveal a single tuple
  // dst is pre-allocated to input.size() / 8 bytes
  void revealBits(Integer &input, int output_party, signed char * dst) {
	int length = input.size();

	int tupleBytes = length / 8;

	bool * b = new bool[length];
	block *bits =  (block *) input.bits;

	ProtocolExecution::prot_exec->reveal(b, output_party, bits,  length);

	bool *readPos = b;
	int8_t *writePos = dst + tupleBytes - 1; // last idx
	// reverse the process in setInput
	for(int i = 0; i < tupleBytes; ++i) {
			*writePos = boolsToByte(readPos);
			readPos += 8; // next bytes
			--writePos;
	}


  delete[] b;
}


// take in inputs of alice and bob
// and unions them into a single array of secret shared tuples
Integer* secretShare(int party, NetIO * io) {



    int aliceSize, bobSize;
    aliceSize = bobSize = inputLength; //  in tuples

    if (party == ALICE) {
        io->send_data(&aliceSize, 4);
        io->flush();
        io->recv_data(&bobSize, 4);
        io->flush();
    } else if (party == BOB) {
        io->recv_data(&aliceSize, 4);
        io->flush();
        io->send_data(&bobSize, 4);
        io->flush();
    }

    unionedTupleCount = aliceSize + bobSize;

    Integer * res = new Integer[unionedTupleCount];  // enough space for all inputs

    Bit * tmp = new Bit[tupleWidth * unionedTupleCount]; //  bit array of inputs
    Bit *tmpPtr = tmp;

    Batcher bobBatcher;

    int bobBits = bobSize * tupleWidth;
    int aliceBits = aliceSize * tupleWidth;

    for (int i = 0; i < aliceBits; ++i) {
    	*tmpPtr = Bit((ALICE==party) ? inputBits[i] : 0, ALICE);
    	++tmpPtr;
    }

    if(party == ALICE)
    	cout << endl;

    for (int i = 0; i < bobBits; ++i) {
    	bobBatcher.add<Bit>((party == BOB) ? inputBits[i]:0);
     }

    bobBatcher.make_semi_honest(BOB);



	// append all of bob's bits to tmp
    for (int i = 0; i < bobBits; ++i) {
        *tmpPtr = bobBatcher.next<Bit>();
        ++tmpPtr;
	}



   	// resetting cursor
	tmpPtr = tmp;

	// create a 2D array of secret-shared bits
	// each index is a tuple
	// expect alice's first input, 0414, to be 808726836
    for(int i = 0; i < aliceSize + bobSize; ++i) {
        res[i] = Integer(tupleWidth, tmpPtr);
        tmpPtr += tupleWidth;
     }

    return res;


}


unsigned char reverse(unsigned char b) {
   b = (b & 0xF0) >> 4 | (b & 0x0F) << 4;
   b = (b & 0xCC) >> 2 | (b & 0x33) << 2;
   b = (b & 0xAA) >> 1 | (b & 0x55) << 1;
   return b;
}


int8_t boolsToByte(bool *src) {
	int8_t dst = 0;

	for(int i = 0; i < 8; ++i) {
		dst |= (src[i] << i);
	}

	return dst;
}

bool *bytesToBool(int8_t *bytes, int byteCount) {
	bool *ret = new bool[byteCount * 8];

	bool *writePos = ret;

	for(int i = 0; i < byteCount; ++i) {
		uint8_t b = bytes[i];
		for(int j = 0; j < 8; ++j) {
			*writePos = ((b & (1<<j)) != 0);
			++writePos;
		}
	}
	return ret;
}


public:

	//  len/width passed in as bytes
	// int8_t because of javacpp's type management
	void setInput(int len, int aTupleWidth, const int8_t *bytes) {
		inputLength = len / aTupleWidth;
		tupleWidth = aTupleWidth * 8;
		int tupleCount = len / aTupleWidth;
		int8_t *reversed = new int8_t[len];

		// reverse byte order for emp
		// also reverses order of inputs
		int srcIdx = aTupleWidth - 1;


		for(int i = 0; i < tupleCount; ++i) {
			for(int j = 0; j < aTupleWidth; ++j) {
				reversed[i*aTupleWidth + j] = bytes[srcIdx];
				--srcIdx;
			}
			srcIdx += 2 * aTupleWidth;
		}

		// convert byte array to bit array
		inputBits = bytesToBool(reversed, len);
		delete[] reversed;

	}


	// in bytes
	int getOutputSize() {
		return unionedTupleCount * tupleWidth / 8;
	}


	signed char * run(int party, int port) {

		std::cout << "starting run in emp! party=" << party << " port=" << port <<  " alice host=" << aliceHost <<  std::endl;
		NetIO * io = new NetIO((party==ALICE ? nullptr : aliceHost.c_str()), port);

		setup_semi_honest(io, party);

		Integer *unioned = secretShare(party, io);



		sort(unioned, unionedTupleCount);


		long outputSize = unionedTupleCount * tupleWidth / 8; // in bytes


		outputBytes =  new signed char[outputSize];
		signed char *outputPtr = outputBytes;
		signed char *tupleXOR = new signed char[tupleWidth/8];

		for(int i = 0; i < unionedTupleCount; ++i) {
			revealBits(unioned[i], XOR, tupleXOR);
			memcpy(outputPtr, tupleXOR, tupleWidth/8);
			outputPtr += tupleWidth/8;
		}

		io->flush();
		delete io;
		delete[] tupleXOR;

		cout << "Completed emp program!" << endl;

        return outputBytes;
}


 void cleanUp() {
		delete[] inputBits;
		delete[] outputBytes;
	}



	// expects as arguments party (1 = alice, 2 = bob) plus the port it will run the protocols over

int main(int argc, char** argv) {
	int party, port;
	parse_party_and_port(argv, 2, &party, &port);
	run(party, port);
	return 0;
}
}; // end class
} // end namespace

