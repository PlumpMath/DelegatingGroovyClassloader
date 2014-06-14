## Simple Delegating Groovy ClassLoader ##


### Description ###

This a simple project in using a custom ClassLoader.<br/>
The simple idea is to embed GroovyClassLoader, and<br/> 
so, be able to use all abilities of Groovy Language, but<br/>
it isn't enough. The target is to be able to expose<br/>
a normal ClassLoader and be able to unload the different<br/>
 classes loaded and also all Groovy Environment, if needed.<br/>
<br/>
To make a complete demonstration, i use a common<br/>
contract, the interface sample.module.RenderingModule,<br/>
and a simple Main class that host different tests.<br/>
<br/>
The concrete implementation of the common contract are <br/>
expressed by groovy script (in reality simple java class).<br/>
The test uses generated class from groovy, thanks to <br/>
RenderingModule interface. As bonus I can add external<br/>
 library, used by the script.<br/>

I've make direct use of ClassLoader but if you want a<br/>
more transparent use, you can adopt the strategy of<br/>
 changing context classloader for current Thread<br/>

<pre><code>
Thread.currentThread().setContextClassLoader(...);
</code></pre>

### Compile And Use The Tests ###

To compile you need, in classpath, also groovy jar, but to execute<br/>
you only need it.fago.groovy and sample.module packages!<br/>

Run the different tests using the following parameters, if<br/>
you want to see the unloading effect, when resetting or destroying</br>
the ClassLoader.</br>

<pre><code>
-verbose:class -ms2m -mx2m
</code></pre>

### Example ###

We start setting where's Groovy binaries are<br/>

<pre><code>
URL[] groovyLibs = new URL[] { ... };
</code></pre>

than we create the ClassLoader<br/>


<pre><code>
DelegatingClassLoader loader = new DelegatingClassLoader();

loader.init(groovyLibs);
</code></pre>

thant we can add external libraries, as follow:<br/>

<pre><code>
URL[] libs = new URL[] { ... };

loader.addURL(libs[0]);
</code></pre>

we can now generate the concrete RenderingModule

<pre><code>
Class&lt;? extends RenderingModule&gt; clz = loader.&lt;RenderingModule&gt; generateClassFromScript(script);

RenderingModule mod = clz.newInstance();
</code></pre>

once generate, we can also load the generated class:

<pre><code>
loader.loadClass("sample.module.SampleModule");
</code></pre>

We can also reset the ClassLoader to purge loaded libraries
and generated classes or to purge all the environment:

<pre><code>
loader.resetPartially();
</code></pre>

<pre><code>
loader.resetTotally();
</code></pre>

When we have ended with this ClassLoader, we can
ensure a cleaned environment as follow:

<pre><code>
loader.destroy();
</code></pre>
				
