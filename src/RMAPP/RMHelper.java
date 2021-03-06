package RMAPP;


/**
* RMAPP/RMHelper.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from src/RM.idl
* Monday, November 22, 2021 6:25:09 o'clock PM EST
*/

abstract public class RMHelper
{
  private static String  _id = "IDL:RMAPP/RM:1.0";

  public static void insert (org.omg.CORBA.Any a, RMAPP.RM that)
  {
    org.omg.CORBA.portable.OutputStream out = a.create_output_stream ();
    a.type (type ());
    write (out, that);
    a.read_value (out.create_input_stream (), type ());
  }

  public static RMAPP.RM extract (org.omg.CORBA.Any a)
  {
    return read (a.create_input_stream ());
  }

  private static org.omg.CORBA.TypeCode __typeCode = null;
  synchronized public static org.omg.CORBA.TypeCode type ()
  {
    if (__typeCode == null)
    {
      __typeCode = org.omg.CORBA.ORB.init ().create_interface_tc (RMAPP.RMHelper.id (), "RM");
    }
    return __typeCode;
  }

  public static String id ()
  {
    return _id;
  }

  public static RMAPP.RM read (org.omg.CORBA.portable.InputStream istream)
  {
    return narrow (istream.read_Object (_RMStub.class));
  }

  public static void write (org.omg.CORBA.portable.OutputStream ostream, RMAPP.RM value)
  {
    ostream.write_Object ((org.omg.CORBA.Object) value);
  }

  public static RMAPP.RM narrow (org.omg.CORBA.Object obj)
  {
    if (obj == null)
      return null;
    else if (obj instanceof RMAPP.RM)
      return (RMAPP.RM)obj;
    else if (!obj._is_a (id ()))
      throw new org.omg.CORBA.BAD_PARAM ();
    else
    {
      org.omg.CORBA.portable.Delegate delegate = ((org.omg.CORBA.portable.ObjectImpl)obj)._get_delegate ();
      RMAPP._RMStub stub = new RMAPP._RMStub ();
      stub._set_delegate(delegate);
      return stub;
    }
  }

  public static RMAPP.RM unchecked_narrow (org.omg.CORBA.Object obj)
  {
    if (obj == null)
      return null;
    else if (obj instanceof RMAPP.RM)
      return (RMAPP.RM)obj;
    else
    {
      org.omg.CORBA.portable.Delegate delegate = ((org.omg.CORBA.portable.ObjectImpl)obj)._get_delegate ();
      RMAPP._RMStub stub = new RMAPP._RMStub ();
      stub._set_delegate(delegate);
      return stub;
    }
  }

}
