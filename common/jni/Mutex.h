#ifndef MUTEX_H
#define MUTEX_H

#include <pthread.h>

namespace util
{

class Mutex
{
public:
	Mutex();
	~Mutex();
	void lock();
	void unlock();
    bool isLocked();
    bool tryLock();

    Mutex(Mutex const&); //needed for CCObject
    Mutex& operator=(Mutex const&); //needed for CCObject
private:
	void init();
	pthread_mutex_t _mutex;
    bool _isLocked;
};

}

#endif


