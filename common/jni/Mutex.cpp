
#include "Mutex.h"

namespace util
{

    Mutex::Mutex()
    {
    	init();
    }
    
    Mutex::Mutex(Mutex const& aMutex)
    {
    	init();
    }

    void Mutex::init()
    {
    	pthread_mutexattr_t attr;
    	pthread_mutexattr_init(&attr);
    	pthread_mutexattr_settype(&attr, PTHREAD_MUTEX_RECURSIVE_NP);
    	pthread_mutex_init(&_mutex, &attr);
    	_isLocked = false;
    }

    Mutex::~Mutex()
    {
        pthread_mutex_destroy(&_mutex);
    }

    void Mutex::lock()
    {
        pthread_mutex_lock(&_mutex);
        _isLocked = true;
    }

    void Mutex::unlock()
    {
        pthread_mutex_unlock(&_mutex);
        _isLocked = false;
    }

    bool Mutex::isLocked()
    {
        return _isLocked;
    }
    
    Mutex& Mutex::operator=(Mutex const&)
    {
        //do nothing
        return *this;
    }

    bool Mutex::tryLock()
    {
        _isLocked = (pthread_mutex_trylock(&_mutex)==0)?true:false;
        return _isLocked;
    }
}
