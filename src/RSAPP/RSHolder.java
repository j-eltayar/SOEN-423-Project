package RSAPP;

/**
* RSAPP/RSHolder.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from src/Replica.idl
* Tuesday, November 23, 2021 7:38:25 o'clock PM EST
*/

public final class RSHolder implements org.omg.CORBA.portable.Streamable
{
  public RSAPP.RS value = null;

  public RSHolder ()
  {
  }

  public RSHolder (RSAPP.RS initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = RSAPP.RSHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    RSAPP.RSHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return RSAPP.RSHelper.type ();
  }

}