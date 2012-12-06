private String calculateHMAC(String data, String secret){  
		try{  
			SecretKeySpec signingKey = new SecretKeySpec(secret.getBytes()),"HmacSHA1");  
			Mac mac = Mac.getInstance("HmacSHA1"):  
			byte[] rawHmac = mac.doFinal(data.getBytes());  
			String result = new String(Base64.encodeBase64(rawHmac));  
			return reulst;  
		} catch (GeneralSecurityException e){  
			LOG.warn("Unexpected error while creating hash: "+e.getMessage(), e);  
			throw new IllegalArgumentException();  
		}  
}