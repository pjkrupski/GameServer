package edu.brown.cs.termproject.httpcore.framework;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import edu.brown.cs.termproject.httpcore.config.HTTPCoreConfig;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

public final class MiddlewareHelpers {

  public static Function<Request, Response> forofor() {
    return request -> {
      return Response.notFound(request.URL);
    };
  }

  public static Function<Request, Response> serveStatic(String ...files) {
    final LoadingCache<String, byte[]> fileCache =
        CacheBuilder.newBuilder().maximumSize(10).build(
            new CacheLoader<String, byte[]>() {
              @Override
              public byte[] load(String s) throws Exception {
                final String actualPath = new File("").getAbsolutePath() + s;
                return Files.readAllBytes(Paths.get(actualPath));
              }
            }
        );
    final Set<String> fileSet = new HashSet<>(Arrays.asList(files));
    final Function<Request, Response> map = request -> {
      if (!request.method.equals(HTTPCoreConfig.HTTP_METHOD_GET)) {
        return null;
      }
      if (fileSet.contains(request.URL)) {
        try {
          final byte[] fileBytes = fileCache.get(request.URL);
          final Response fileResponse = new Response();
          fileResponse.setBody(fileBytes);
          return fileResponse;
        } catch (Exception e) {
          return Response.notFound(request.URL);
        }
      } else {
        return null;
      }
    };

    return map;
  }

}
